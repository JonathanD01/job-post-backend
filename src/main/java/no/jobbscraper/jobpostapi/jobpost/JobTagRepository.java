package no.jobbscraper.jobpostapi.jobpost;

import no.jobbscraper.jobpostapi.jobpost.JobTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JobTagRepository extends JpaRepository<JobTag, Long> {

    Optional<JobTag> findByTagLikeIgnoreCase(String tag);
}