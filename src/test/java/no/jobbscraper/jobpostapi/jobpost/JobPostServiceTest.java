package no.jobbscraper.jobpostapi.jobpost;

import net.datafaker.Faker;
import no.jobbscraper.jobpostapi.exception.BadSecretKeyException;
import no.jobbscraper.jobpostapi.exception.JobPostNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class JobPostServiceTest {

    private Faker faker;

    @InjectMocks
    private JobPostService underTest;
    @Mock
    private JobPostRepository jobPostRepository;
    @Mock
    private JobTagRepository jobTagRepository;
    @Mock
    private JobDefinitionRepository jobDefinitionRepository;
    @Mock
    private JobPostDtoMapper jobPostDTOMapper;

    @BeforeEach
    void setUp() {
        faker = new Faker();
        ReflectionTestUtils.setField(underTest, "secretKey", "mySuperSecretKey");
    }

    @Test
    @DisplayName("It should return all job posts")
    void itShouldGetAllJobPosts() {
        // Given
        JobPostGetRequest jobPostGetRequest =
                new JobPostGetRequest(null, null, null, null, null);
        int page = 0;
        int size = 12;

        List<JobPost> jobPosts = JobPostUtil.getJobPosts(faker.random().nextInt(15, 30));

        Page<JobPost> jobPostPage = new PageImpl<>(jobPosts);

        // When
        when(jobPostRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(jobPostPage);

        // Then
        Page<JobPostDto> response = underTest.getAllJobPosts(jobPostGetRequest, page, size);

        assertThat(response.getTotalElements()).isEqualTo(jobPosts.size());
    }

    @Test
    @DisplayName("It should get a JobPostDto from jobPostId")
    void itShouldGetJobPostFromId() {
        // Given
        Long jobPostId = faker.random().nextLong(1, 100);

        JobPost jobPost = JobPostUtil.getJobPost(); // Create a JobPost instance
        Optional<JobPost> optionalJobPost = Optional.of(jobPost); // Wrap it in an Optional

        // Map the JobPost to a JobPostDto
        JobPostDto jobPostDto = JobPostUtil.getJobPostDtoFrom(jobPost);

        // When
        when(jobPostRepository.findById(jobPostId)).thenReturn(optionalJobPost);
        when(jobPostDTOMapper.apply(optionalJobPost.get())).thenReturn(jobPostDto);

        // Then
        JobPostDto response = underTest.getJobPostFromId(jobPostId);

        assertThat(response.url()).isEqualTo(jobPost.getUrl());
        assertThat(response.companyName()).isEqualTo(jobPost.getCompanyName());
        assertThat(response.companyImageUrl()).isEqualTo(jobPost.getCompanyImageUrl());
        assertThat(response.imageUrl()).isEqualTo(jobPost.getImageUrl());
        assertThat(response.title()).isEqualTo(jobPost.getTitle());
        assertThat(response.description()).isEqualTo(jobPost.getDescription());
        assertThat(response.deadline()).isEqualTo(jobPost.getDeadline());
    }

    @Test
    @DisplayName("It should not get JobPostDto from jobPostId")
    void itShouldNotGetJobPostFromId() {
        // Given
        Long jobPostId = faker.random().nextLong(1, 100);

        // When
        when(jobPostRepository.findById(jobPostId)).thenReturn(Optional.empty());

        // Then
        assertThatThrownBy(() -> underTest.getJobPostFromId(jobPostId))
                .isInstanceOf(JobPostNotFoundException.class)
                .hasMessage("Job post with id " + jobPostId + " was not found");
    }

    @Test
    @DisplayName("It should create job post(s)")
    void itShouldCreateJobPosts() {
        // Given
        List<JobPostCreateDto> jobPostCreateDtos = JobPostUtil.getJobPosts(faker.random().nextInt(15, 30)).stream()
                .map(JobPostUtil::getJobPostCreateDtoFrom)
                .toList();

        JobPostCreateRequest jobPostCreateRequest = new JobPostCreateRequest(jobPostCreateDtos);

        String secretKey = "mySuperSecretKey";

        JobPost jobPostWithId = JobPostUtil.getJobPost();
        jobPostWithId.setId(faker.random().nextLong(1, 100));

        // When
        when(jobPostRepository.save(any())).thenReturn(jobPostWithId);

        List<Long> response = underTest.createJobPosts(jobPostCreateRequest, secretKey);

        // Then
        assertThat(response.size()).isEqualTo(jobPostCreateDtos.size());
    }

    @Test
    @DisplayName("It not should create any job post(s)")
    void itShouldNotCreateJobPosts() {
        // Given
        JobPostCreateRequest jobPostCreateRequest = new JobPostCreateRequest(Collections.emptyList());

        String secretKey = "mySuperSecretKey";

        // When
        List<Long> response = underTest.createJobPosts(jobPostCreateRequest, secretKey);

        // Then
        assertThat(response.size()).isEqualTo(0);
    }

    @Test
    @DisplayName("It not should create any job post(s) if secret key is invalid")
    void itShouldNotCreateJobPostsIfSecretKeyInvalid() {
        // Given
        JobPostCreateRequest jobPostCreateRequest = new JobPostCreateRequest(Collections.emptyList());
        String givenSecretKey = UUID.randomUUID().toString();

        // When
        // Then
        assertThatThrownBy(() -> underTest.createJobPosts(jobPostCreateRequest, givenSecretKey))
                .isInstanceOf(BadSecretKeyException.class)
                .hasMessage("Invalid access");
    }

    @Test
    @DisplayName("It not should create any job post(s) if secret key is null")
    void itShouldNotCreateJobPostsIfSecretKeyNull() {
        // Given
        JobPostCreateRequest jobPostCreateRequest = new JobPostCreateRequest(Collections.emptyList());
        String givenSecretKey = null;

        // When
        // Then
        assertThatThrownBy(() -> underTest.createJobPosts(jobPostCreateRequest, givenSecretKey))
                .isInstanceOf(BadSecretKeyException.class)
                .hasMessage("Invalid access");
    }
}