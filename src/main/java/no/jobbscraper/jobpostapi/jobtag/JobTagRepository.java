package no.jobbscraper.jobpostapi.jobtag;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface JobTagRepository extends CrudRepository<JobTag, Long> {

    @Query("select j from JobTag j where lower(j.tag) = lower(?1)")
    Optional<JobTag> findByTag(String tag);
}