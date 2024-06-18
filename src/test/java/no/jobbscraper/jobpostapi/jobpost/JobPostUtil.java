package no.jobbscraper.jobpostapi.jobpost;

import net.datafaker.Faker;
import no.jobbscraper.jobpostapi.jobdefinition.JobDefinition;
import no.jobbscraper.jobpostapi.jobtag.JobTag;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.random.RandomGenerator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JobPostUtil {

    private static final Faker faker = new Faker();

    private static final SecureRandom secureRandom = new SecureRandom();

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
                        faker.lorem().characters(randomInt()),
                        faker.lorem().characters(randomInt()),
                        faker.lorem().characters(randomInt()))
                .map(JobTag::new)
                .collect(Collectors.toSet());

        Set<JobDefinition> jobDefinitions = Set.of(
                new JobDefinition(faker.lorem().characters(randomInt()),
                        faker.lorem().characters(randomInt())),
                new JobDefinition(faker.lorem().characters(randomInt()),
                        faker.lorem().characters(randomInt())),
                new JobDefinition(faker.lorem().characters(randomInt()),
                        faker.lorem().characters(randomInt())));

        String url = faker.internet().url();
        String companyName = faker.company().name();
        String companyImageUrl = faker.internet().image();
        String imageUrl = faker.internet().image();
        String title = faker.book().title();
        String description = faker.lorem().characters(500, 600);
        LocalDate deadline = faker.date().birthdayLocalDate();

        return new JobPost.Builder(url, imageUrl, title)
                .setDescription(description)
                .setCompanyName(companyName)
                .setCompanyImageUrl(companyImageUrl)
                .setDeadline(deadline)
                .setTags(jobTags)
                .setJobDefinitions(jobDefinitions)
                .build();
    }

    public static JobPostDto getJobPostDtoFrom(JobPost jobPost) {
        return new JobPostDto(jobPost.getId(), jobPost.getCreatedAt(), jobPost.getUrl(), jobPost.getCompanyName(),
                jobPost.getCompanyImageUrl(), jobPost.getImageUrl(), jobPost.getTitle(), jobPost.getDescription(),
                jobPost.getDeadline(), jobPost.getJobTags(), jobPost.getJobDefinitions());
    }

    public static JobPostCreateDto getJobPostCreateDtoFrom(JobPost jobPost) {
        return new JobPostCreateDto(jobPost.getUrl(), jobPost.getCompanyName(), jobPost.getCompanyImageUrl(),
                jobPost.getImageUrl(), jobPost.getTitle(), jobPost.getDescription(),
                jobPost.getDeadline(), jobPost.getJobTags(), jobPost.getJobDefinitions());
    }

    private static int randomInt() {
        return secureRandom.nextInt(1, 200);
    }

}
