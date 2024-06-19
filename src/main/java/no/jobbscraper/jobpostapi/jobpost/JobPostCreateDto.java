package no.jobbscraper.jobpostapi.jobpost;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import no.jobbscraper.jobpostapi.deserializer.CustomJobDefinitionDeserializer;
import no.jobbscraper.jobpostapi.jobdefinition.JobDefinition;
import no.jobbscraper.jobpostapi.jobtag.JobTag;

import java.time.LocalDate;
import java.util.Set;

public record JobPostCreateDto(
        @JsonProperty("url")
        @NotBlank(message = "Job post must have a url")
        String url,

        @JsonProperty("company_name")
        @NotBlank(message = "Job post must have a company name")
        String companyName,

        @JsonProperty("company_image_url")
        @Nullable
        String companyImageUrl,

        @JsonProperty("image_url")
        @Nullable
        String imageUrl,

        @JsonProperty("title")
        @NotBlank(message = "Job post must have a title")
        String title,

        @JsonProperty("description")
        @NotBlank(message = "Job post must have a description")
        String description,

        @JsonProperty("deadline")
        @Nullable
        LocalDate deadline,

        @JsonProperty("job_tags")
        @Nullable
        Set<JobTag> jobTags,

        @JsonProperty("job_definitions")
        @JsonDeserialize(using = CustomJobDefinitionDeserializer.class)
        @Nullable
        Set<JobDefinition> jobDefinitions) {

}