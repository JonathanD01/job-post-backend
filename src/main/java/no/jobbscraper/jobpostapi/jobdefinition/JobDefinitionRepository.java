package no.jobbscraper.jobpostapi.jobdefinition;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface JobDefinitionRepository extends JpaRepository<JobDefinition, Long> {

    @Query("select j from JobDefinition j where lower(j.key) = lower(?1) and lower(j.value) = lower(?2)")
    Optional<JobDefinition> findByKeyAndValue(String key, String value);

}