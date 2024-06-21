package no.jobbscraper.jobpostapi.jobpost;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;

import java.util.Optional;

public interface JobPostRepository extends CrudRepository<JobPost, Long> {

    @NonNull
    @EntityGraph(attributePaths = {"jobTags", "jobDefinitions"})
    Optional<JobPost> findById(@NonNull Long id);

    boolean existsByUrl(String url);
}
