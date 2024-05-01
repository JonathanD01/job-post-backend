package no.jobbscraper.jobpostapi.jobpost;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JobDefinitionRepository extends JpaRepository<JobDefinition, Long> {


    Optional<JobDefinition> findByKeyLikeIgnoreCaseAndValueLikeIgnoreCase(String key, String value);
}