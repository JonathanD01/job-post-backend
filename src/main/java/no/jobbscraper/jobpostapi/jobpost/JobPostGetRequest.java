package no.jobbscraper.jobpostapi.jobpost;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Null;
import org.springframework.web.bind.annotation.RequestParam;

public record JobPostGetRequest(
        @Nullable
        String query,
        @Nullable
        String position,
        @Nullable
        String sector,
        @Nullable
        String municipality,
        @Nullable
        String deadline){
}
