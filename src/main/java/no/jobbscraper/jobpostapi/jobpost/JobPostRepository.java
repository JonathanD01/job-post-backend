package no.jobbscraper.jobpostapi.jobpost;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JobPostRepository extends JpaRepository<JobPost, Long>,
        JpaSpecificationExecutor<JobPost> {

    @NonNull
    @Override
    @EntityGraph(attributePaths = {"jobTags", "jobDefinitions"})
    Optional<JobPost> findById(@NonNull Long id);

    @NonNull
    @Override
    @EntityGraph(attributePaths = {"jobTags", "jobDefinitions"})
    Page<JobPost> findAll(@NonNull Specification<JobPost> spec, @NonNull Pageable pageable);

    boolean existsByUrl(String url);
}
