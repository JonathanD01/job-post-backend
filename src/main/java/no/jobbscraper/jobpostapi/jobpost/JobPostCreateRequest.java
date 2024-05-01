package no.jobbscraper.jobpostapi.jobpost;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record JobPostCreateRequest(
        @JsonProperty("job_posts")
        @NotEmpty(message = "job_posts cannot be empty")
        List<JobPostCreateDto> jobPosts
) {

}
