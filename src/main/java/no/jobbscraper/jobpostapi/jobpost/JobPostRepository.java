package no.jobbscraper.jobpostapi.jobpost;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

public interface JobPostRepository extends JpaRepository<JobPost, Long>,
        JpaSpecificationExecutor<JobPost> {

    @NonNull
    @Override
    @EntityGraph(attributePaths = {"jobTags", "jobDefinitions"})
    Optional<JobPost> findById(@NonNull Long id);

    @NonNull
    @Override
    @EntityGraph(attributePaths = {"jobTags", "jobDefinitions"})
    List<JobPost> findAll(@NonNull Specification<JobPost> spec);

    boolean existsByUrl(String url);
}
