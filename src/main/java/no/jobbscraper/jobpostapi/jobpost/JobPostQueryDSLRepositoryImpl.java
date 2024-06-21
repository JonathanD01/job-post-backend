package no.jobbscraper.jobpostapi.jobpost;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.JPQLQuery;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public class JobPostQueryDSLRepositoryImpl extends QuerydslRepositorySupport implements JobPostRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    private final JobPostDtoMapper jobPostDtoMapper;

    public JobPostQueryDSLRepositoryImpl() {
        super(JobPost.class);
        this.jobPostDtoMapper = new JobPostDtoMapper();
    }

    @Override
    public Page<JobPostDto> findAll(JobPostGetRequest jobPostGetRequest, Pageable pageable) {
        var jobPostTable = QJobPost.jobPost;

        // Fetch JobPost IDs with the criteria
        JPQLQuery<Long> idQuery = from(jobPostTable)
                .select(jobPostTable.id)
                .where(filterPredicate(jobPostGetRequest))
                .orderBy(orderPredicate(jobPostGetRequest.deadline()))
                .limit(50L);

        // Apply pagination to the ID query
        long total = idQuery.fetchCount();

        List<Long> jobPostIds = idQuery
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // Fetch JobPosts by IDs
        List<JobPost> jobPosts = from(jobPostTable)
                .leftJoin(jobPostTable.jobTags).fetchJoin()
                .leftJoin(jobPostTable.jobDefinitions).fetchJoin()
                .where(jobPostTable.id.in(jobPostIds))
                .orderBy(orderPredicate(jobPostGetRequest.deadline()))
                .fetch();

        var jobPostDtos = jobPosts.stream()
                .map(jobPostDtoMapper)
                .toList();

        return new PageImpl<>(jobPostDtos, pageable, total);
    }

    private OrderSpecifier<LocalDate> orderPredicate(String deadline) {
        if (deadline != null) {
            return JobPostPredicates.orderBySpecificDeadline(deadline);
        }
        return JobPostPredicates.orderByClosestDeadline();
    }

    private Predicate filterPredicate(JobPostGetRequest jobPostGetRequest) {
        return  JobPostPredicates.hasUrl()
                .and(JobPostPredicates.hasDeadlineNotPassed())
                .and(JobPostPredicates.hasSpecificDeadline(jobPostGetRequest.deadline()))
                .and(JobPostPredicates.isQueryInTitleOrDescription(jobPostGetRequest.query()))
                .and(JobPostPredicates.hasPosition(entityManager, jobPostGetRequest.municipality()))
                .and(JobPostPredicates.hasJobDefinition(entityManager, "Stilling", jobPostGetRequest.position()))
                .and(JobPostPredicates.hasJobDefinition(entityManager, "Sektor", jobPostGetRequest.sector()));
    }
}