package no.jobbscraper.jobpostapi.jobdefinition;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface JobDefinitionRepository extends CrudRepository<JobDefinition, Long> {

    @Query("select j from JobDefinition j where lower(j.key) = lower(?1) and lower(j.value) = lower(?2)")
    Optional<JobDefinition> findByKeyAndValue(String key, String value);
}