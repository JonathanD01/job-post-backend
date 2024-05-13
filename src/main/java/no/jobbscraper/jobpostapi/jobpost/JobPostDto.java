package no.jobbscraper.jobpostapi.jobpost;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Date;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public record JobPostDto(
        Long id,
        @JsonProperty("created_at")
        Date createdAt,
        String url,
        @JsonProperty("company_name")
        String companyName,
        @JsonProperty("company_image_url")
        String companyImageUrl,
        @JsonProperty("image_url")
        String imageUrl,
        String title,
        String description,
        @JsonFormat(pattern = "dd-MM-yyyy")
        @DateTimeFormat(pattern = "dd-MM-yyyy")
        LocalDate deadline,
        @JsonProperty("job_tags")
        Set<JobTag> jobTags,
        @JsonProperty("job_description")
        Set<JobDefinition> jobDefinitions) {
}
