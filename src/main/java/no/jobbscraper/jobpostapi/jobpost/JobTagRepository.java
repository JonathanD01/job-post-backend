package no.jobbscraper.jobpostapi.jobpost;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface JobTagRepository extends JpaRepository<JobTag, Long> {

    @Query("select j from JobTag j where lower(j.tag) = lower(?1)")
    Optional<JobTag> findByTag(String tag);
}