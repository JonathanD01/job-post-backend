package no.jobbscraper.jobpostapi.jobpost;

import net.datafaker.Faker;
import no.jobbscraper.jobpostapi.PostgreSQLContainerInitializer;
import no.jobbscraper.jobpostapi.response.ResponseType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_CLASS;

@ActiveProfiles("test")
@DirtiesContext
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(initializers = {PostgreSQLContainerInitializer.class})
@Sql(value = "/test-data.sql", executionPhase = BEFORE_TEST_CLASS)
class JobPostIntegrationTest  {

    private static final String JOB_POST_PAH = "/api/v1/jobposts";

    @Value("${secret_key}")
    private String secretKey;

    @Autowired
    private JobPostRepository jobPostRepository;

    @Autowired
    private WebTestClient webTestClient;

    private Faker faker;

    @BeforeEach
    void setUp() {
        faker = new Faker();
    }

    @AfterEach
    void tearDown() {

    }

    @Test
    @DisplayName("It should get a list of jobposts that are ordered by deadline in desc order")
    void itShouldGetAllJobPosts() {
        // Given
        int page = 0;
        int size = faker.random().nextInt(2, 6);

        // When
        // Then
        webTestClient.method(HttpMethod.GET)
                .uri(JOB_POST_PAH + "?page={page}&size={size}", page, size)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.response").isEqualTo(ResponseType.SUCCESS.name())
                .jsonPath("$.result.pageable.pageNumber").isEqualTo(0)
                .jsonPath("$.result.pageable.pageSize").isEqualTo(size)
                .jsonPath("$.result.numberOfElements").isEqualTo(size)
                .jsonPath("$.result.content").isNotEmpty()
                .jsonPath("$.result.content[0].deadline").exists()
                .jsonPath("$.result.content[1].deadline").exists();
    }

    @Test
    @DisplayName("It should return a no content response if jobposts are empty")
    void itShouldNotGetAllJobPosts() {
        // Given
        int page = faker.random().nextInt(10, 20);
        int size = faker.random().nextInt(12, 20);

        // When
        // Then
        webTestClient.method(HttpMethod.GET)
                .uri(JOB_POST_PAH + "?page={page}&size={size}", page, size)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatusCode.valueOf(204));
    }

    @Test
    @DisplayName("It should return a jobpost if id exists")
    void itShouldGetJobPostFromId() {
        // Given
        int jobPostId = faker.options().option(new int[]{155, 255, 355, 655, 755, 855, 955, 1550});

        // When
        // Then
        webTestClient.method(HttpMethod.GET)
                .uri(JOB_POST_PAH + "/{jobPostId}", jobPostId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.response").isEqualTo(ResponseType.SUCCESS.name())
                .jsonPath("$.result.id").isEqualTo(jobPostId)
                .jsonPath("$.result.url").isNotEmpty()
                .jsonPath("$.result.created_at").isNotEmpty()
                .jsonPath("$.result.job_description").isArray()
                .jsonPath("$.result.job_tags").isArray();
    }

    @Test
    @DisplayName("It should not return a jobpost if id doesn't exist")
    void itShouldNotGetJobPostFromId() {
        // Given
        int jobPostId = faker.random().nextInt(100, 500);

        // When
        // Then
        webTestClient.method(HttpMethod.GET)
                .uri(JOB_POST_PAH + "/{jobPostId}", jobPostId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatusCode.valueOf(400))
                .expectBody()
                .jsonPath("$.response").isEqualTo(ResponseType.FAILED.name())
                .jsonPath("$.errors").isArray()
                .jsonPath("$.errors[0].message").isEqualTo("Job post with id " + jobPostId
                        + " was not found")
                .jsonPath("$.errors[0].http_status").isEqualTo(HttpStatus.BAD_REQUEST.name());
    }

    @Test
    void itShouldCreateJobPost() {
        // Given
        List<JobPostCreateDto> jobPostCreateDtos = JobPostUtil.getJobPosts(faker.random().nextInt(15, 30)).stream()
                .map(JobPostUtil::getJobPostCreateDtoFrom)
                .toList();

        JobPostCreateRequest jobPostCreateRequest = new JobPostCreateRequest(jobPostCreateDtos);

        // When
        // Then
        webTestClient.method(HttpMethod.POST)
                .uri(JOB_POST_PAH + "?secretkey={secretkey}", secretKey)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(jobPostCreateRequest), JobPostCreateRequest.class)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.response").isEqualTo(ResponseType.SUCCESS.name())
                .jsonPath("$.result").isArray()
                .jsonPath("$.result").isNotEmpty();
    }
}