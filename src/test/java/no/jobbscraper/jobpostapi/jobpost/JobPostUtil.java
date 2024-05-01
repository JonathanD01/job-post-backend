package no.jobbscraper.jobpostapi.jobpost;

import com.jayway.jsonpath.spi.mapper.GsonMappingProvider;
import net.datafaker.Faker;
import org.springframework.boot.json.GsonJsonParser;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JobPostUtil {

    private static final Faker faker = new Faker();

    private JobPostUtil() {
        throw new AssertionError("Cannot initialize this class");
    }

    public static List<JobPost> getJobPosts(int amountOfJobPosts) {
        List<JobPost> jobPosts = new ArrayList<>();

        for (int i = 0; i < amountOfJobPosts; i++) {
            jobPosts.add(getJobPost());
        }
        return jobPosts;
    }

    public static JobPost getJobPost() {
        Set<JobTag> jobTags = Stream.of(
                        faker.food().ingredient(),
                        faker.food().allergen(),
                        faker.food().dish())
                .map(JobTag::new)
                .collect(Collectors.toSet());

        Set<JobDefinition> jobDefinitions = Set.of(
                new JobDefinition(faker.job().title(), faker.job().keySkills()),
                new JobDefinition(faker.esports().game(), faker.esports().player()),
                new JobDefinition(faker.money().currency(), faker.money().currencyCode()));

        String url = faker.internet().url();
        String companyName = faker.company().name();
        String companyImageUrl = faker.internet().image();
        String imageUrl = faker.internet().image();
        String title = faker.book().title();
        String description = faker.lorem().characters(500, 600);
        boolean deadlineValid = faker.random().nextBoolean();
        LocalDate deadline = faker.date().birthdayLocalDate();

        return new JobPost.Builder(url, imageUrl, title)
                .setDescription(description)
                .setCompanyName(companyName)
                .setCompanyImageUrl(companyImageUrl)
                .setDeadlineValid(deadlineValid)
                .setDeadline(deadline)
                .setTags(jobTags)
                .setJobDefinitions(jobDefinitions)
                .build();
    }

    public static JobPostDto getJobPostDtoFrom(JobPost jobPost) {
        return new JobPostDto(jobPost.getId(), jobPost.getCreatedAt(), jobPost.getUrl(), jobPost.getCompanyName(),
                jobPost.getCompanyImageUrl(), jobPost.getImageUrl(), jobPost.getTitle(), jobPost.getDescription(),
                jobPost.isDeadlineValid(), jobPost.getDeadline(), jobPost.getJobTags(), jobPost.getJobDefinitions());
    }

    public static JobPostCreateDto getJobPostCreateDtoFrom(JobPost jobPost) {
        return new JobPostCreateDto(jobPost.getUrl(), jobPost.getCompanyName(), jobPost.getCompanyImageUrl(),
                jobPost.getImageUrl(), jobPost.getTitle(), jobPost.getDescription(), jobPost.isDeadlineValid(),
                jobPost.getDeadline(), jobPost.getJobTags(), jobPost.getJobDefinitions());
    }

}
