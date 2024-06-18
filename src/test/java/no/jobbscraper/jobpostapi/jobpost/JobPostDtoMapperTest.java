package no.jobbscraper.jobpostapi.jobpost;

import net.datafaker.Faker;
import no.jobbscraper.jobpostapi.jobdefinition.JobDefinition;
import no.jobbscraper.jobpostapi.jobtag.JobTag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class JobPostDtoMapperTest {

    private JobPostDtoMapper jobPostDtoMapper;
    private Faker faker;

    @BeforeEach
    void setUp() {
        jobPostDtoMapper = new JobPostDtoMapper();
        faker = new Faker();
    }

    @Test
    @DisplayName("It should map a JobPost to JobPostDto")
    void itShouldMapJobPostDtoToJobPost() {
        // Given
        Set<JobTag> jobTags = Stream.of(faker.lorem().word(), faker.lorem().word(), faker.lorem().word())
                .map(JobTag::new)
                .collect(Collectors.toSet());

        Set<JobDefinition> jobDefinitions = Set.of(
                new JobDefinition(faker.lorem().word(), faker.lorem().word()),
                new JobDefinition(faker.lorem().word(), faker.lorem().word()),
                new JobDefinition(faker.lorem().word(), faker.lorem().word()));

        String url = faker.internet().url();
        String companyName = faker.company().name();
        String companyImageUrl = faker.internet().image();
        String imageUrl = faker.internet().image();
        String title = faker.book().title();
        String description = faker.lorem().characters(500, 600);
        LocalDate deadline = faker.date().birthdayLocalDate();

        JobPost jobPost = new JobPost.Builder(url, imageUrl, title)
                .setDescription(description)
                .setCompanyName(companyName)
                .setCompanyImageUrl(companyImageUrl)
                .setDeadline(deadline)
                .setTags(jobTags)
                .setJobDefinitions(jobDefinitions)
                .build();

        // When
        JobPostDto response = jobPostDtoMapper.apply(jobPost);

        // Then
        assertThat(response.url()).isEqualTo(jobPost.getUrl());
        assertThat(response.companyName()).isEqualTo(jobPost.getCompanyName());
        assertThat(response.companyImageUrl()).isEqualTo(jobPost.getCompanyImageUrl());
        assertThat(response.imageUrl()).isEqualTo(jobPost.getImageUrl());
        assertThat(response.title()).isEqualTo(jobPost.getTitle());
        assertThat(response.description()).isEqualTo(jobPost.getDescription());
        assertThat(response.deadline()).isEqualTo(jobPost.getDeadline());
        assertThat(response.jobTags().size()).isEqualTo(jobTags.size());
        assertThat(response.jobDefinitions().size()).isEqualTo(jobDefinitions.size());
    }

    @Test
    @DisplayName("It should not map a JobPost to JobPostDTO when JobPost is null")
    void itShouldNotMapJobPostWhenJobPostIsNull() {
        assertThatThrownBy(() -> jobPostDtoMapper.apply(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("The jobpost Dto should not be null");
    }
}