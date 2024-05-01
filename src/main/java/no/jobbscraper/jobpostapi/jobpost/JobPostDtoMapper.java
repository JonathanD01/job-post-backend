package no.jobbscraper.jobpostapi.jobpost;

import java.util.function.Function;

public class JobPostDtoMapper implements Function<JobPost, JobPostDto> {

    @Override
    public JobPostDto apply(JobPost jobPost) {
        if (jobPost == null) {
            throw new NullPointerException("The jobpost Dto should not be null");
        }

        return new JobPostDto(
                jobPost.getId(),
                jobPost.getCreatedAt(),
                jobPost.getUrl(),
                jobPost.getCompanyName(),
                jobPost.getCompanyImageUrl(),
                jobPost.getImageUrl(),
                jobPost.getTitle(),
                jobPost.getDescription(),
                jobPost.isDeadlineValid(),
                jobPost.getDeadline(),
                jobPost.getTags(),
                jobPost.getJobDefinitions()
        );
    }
}
