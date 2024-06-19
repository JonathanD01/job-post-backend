package no.jobbscraper.jobpostapi.jobpost;


import jakarta.annotation.Nullable;

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
