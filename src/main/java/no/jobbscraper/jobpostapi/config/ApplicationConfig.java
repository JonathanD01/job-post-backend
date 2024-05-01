package no.jobbscraper.jobpostapi.config;

import no.jobbscraper.jobpostapi.jobpost.JobPostDtoMapper;
import no.jobbscraper.jobpostapi.response.ResponseUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

    @Bean
    public JobPostDtoMapper jobPostDTOMapper() {
        return new JobPostDtoMapper();
    }

    @Bean
    public ResponseUtil responseUtil() {
        return new ResponseUtil();
    }

}
