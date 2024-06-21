package no.jobbscraper.jobpostapi.jobpost;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface JobPostRepositoryCustom {

    Page<JobPostDto> findAll(JobPostGetRequest jobPostGetRequest, Pageable pageable);
}