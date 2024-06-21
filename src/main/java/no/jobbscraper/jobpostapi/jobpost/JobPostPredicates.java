package no.jobbscraper.jobpostapi.jobpost;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.annotation.Nullable;
import jakarta.persistence.EntityManager;
import no.jobbscraper.jobpostapi.jobdefinition.QJobDefinition;
import no.jobbscraper.jobpostapi.util.GeoUtil;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class JobPostPredicates {

    /**
     * Constructs a BooleanExpression to filter job posts where the URL is not null.
     *
     * @return BooleanExpression to filter job posts where the URL is not null.
     */
    public static BooleanExpression hasUrl() {
        var jobPostTable = QJobPost.jobPost;

        return jobPostTable.url.isNotNull();
    }

    /**
     * Constructs a BooleanExpression to filter job posts where the deadline has not passed.
     *
     * @return BooleanExpression to filter job posts where the deadline has not passed.
     */
    public static BooleanExpression hasDeadlineNotPassed() {
        var jobPostTable = QJobPost.jobPost;

        return jobPostTable.deadline.isNull().or(jobPostTable.deadline.isNotNull()
                .and(jobPostTable.deadline.goe(LocalDate.now())));
    }

    /**
     * Constructs a BooleanExpression to filter job posts based on a specific deadline condition.
     * Returns null if deadline is null.
     *
     * @param deadline The string indicating the specific deadline condition ("nærmest" for upcoming deadlines,
     *                 "lengst unna" for furthest deadlines). If null, returns null.
     * @return BooleanExpression to filter job posts based on the specific deadline condition, or null if deadline is null.
     */
    public static BooleanExpression hasSpecificDeadline(@Nullable String deadline) {
        if (deadline == null) {
            return null;
        }

        var jobPostTable = QJobPost.jobPost;

        BooleanExpression hasDeadline = jobPostTable.deadline.isNotNull();
        BooleanExpression doesNotHaveDeadline = jobPostTable.deadline.isNull();
        BooleanExpression deadlineNotPassed = jobPostTable.deadline.goe(LocalDate.now());

        return switch (deadline.toLowerCase()) {
            case "nærmest" -> Expressions.allOf(deadlineNotPassed, hasDeadline);
            case "lengst unna" -> hasDeadline;
            default -> doesNotHaveDeadline;
        };
    }

    /**
     * Constructs an OrderSpecifier for ordering job posts by closest deadline.
     * Orders job posts by deadline in ascending order. If deadline is null, orders by createdAt.
     *
     * @return OrderSpecifier for ordering job posts by closest deadline.
     */
    public static OrderSpecifier<LocalDate> orderByClosestDeadline() {
        var jobPostTable = QJobPost.jobPost;

        return new CaseBuilder()
                .when(jobPostTable.deadline.isNotNull())
                .then(jobPostTable.deadline)
                .otherwise(LocalDate.now().plusYears(10)) // hack?
                .asc();
    }

    /**
     * Constructs an OrderSpecifier for ordering job posts by a specific deadline.
     * Returns an OrderSpecifier based on the provided deadline or createdAt if deadline is null.
     *
     * @param deadline The string indicating the order of the deadline ("nærmest" for ascending, "lengst unna" for descending).
     *                 If null, orders by createdAt in descending order.
     * @return OrderSpecifier for ordering job posts by deadline or createdAt.
     */
    public static OrderSpecifier<LocalDate> orderBySpecificDeadline(@Nullable String deadline) {
        var jobPostTable = QJobPost.jobPost;

        if (deadline == null) {
            return jobPostTable.createdAt.desc();
        }

        return switch (deadline.toLowerCase()) {
            case "nærmest" -> jobPostTable.deadline.asc();
            case "lengst unna" -> jobPostTable.deadline.desc();
            default -> jobPostTable.createdAt.desc();
        };
    }

    /**
     * Constructs a BooleanExpression to filter job posts based on a query in title or description.
     * Returns null if the query is null.
     *
     * @param query The query string to search for in title or description (case-insensitive).
     * @return BooleanExpression to filter job posts, or null if query is null.
     */
    public static BooleanExpression isQueryInTitleOrDescription(@Nullable String query) {
        if (query == null) {
            return null;
        }

        var jobPostTable = QJobPost.jobPost;

        return jobPostTable.title.containsIgnoreCase(query)
                .or(jobPostTable.description.containsIgnoreCase(query));
    }

    /**
     * Constructs a BooleanExpression to filter job posts based on the position.
     * Returns null if either key or value is null.
     *
     * @param entityManager The EntityManager instance used for querying.
     * @param municipality The position/city value to filter by.
     * @return BooleanExpression to filter job posts, or null if key or value is null.
     */
    public static BooleanExpression hasPosition(EntityManager entityManager, String municipality) {
        if (municipality == null) {
            return null;
        }

        var jobDefinition = QJobDefinition.jobDefinition;
        var jobPostTable = QJobPost.jobPost;

        var municipalities = Arrays.stream(municipality.toLowerCase().replaceAll(" ", "").split(","))
                .flatMap(word -> GeoUtil.municipalityCityMap.get(word).stream())
                .toList();

        List<BooleanExpression> likeConditions = municipalities.stream()
                .map(jobDefinition.value::containsIgnoreCase)
                .toList();

        BooleanExpression combinedCondition = likeConditions.stream()
                .reduce(BooleanExpression::or)
                .orElse(Expressions.FALSE);

        return jobPostTable.id.in(
                new JPAQueryFactory(entityManager)
                        .select(jobPostTable.id)
                        .from(jobPostTable)
                        .join(jobPostTable.jobDefinitions, jobDefinition)
                        .where(
                                jobDefinition.key.equalsIgnoreCase("sted")
                                        .and(combinedCondition)
                        )
        );
    }

    /**
     * Constructs a BooleanExpression to filter job posts based on a job definition key and value.
     * Returns null if either key or value is null.
     *
     * @param entityManager The EntityManager instance used for querying.
     * @param key The job definition key to filter by.
     * @param value The job definition value to filter by (partial match).
     * @return BooleanExpression to filter job posts, or null if key or value is null.
     */
    public static BooleanExpression hasJobDefinition(EntityManager entityManager, String key, String value) {
        if (key == null || value == null) {
            return null;
        }

        var jobDefinition = QJobDefinition.jobDefinition;
        var jobPostTable = QJobPost.jobPost;

        return jobPostTable.id.in(
                new JPAQueryFactory(entityManager)
                        .select(jobPostTable.id)
                        .from(jobPostTable)
                        .join(jobPostTable.jobDefinitions, jobDefinition)
                        .where(
                                jobDefinition.key.equalsIgnoreCase(key)
                                        .and(jobDefinition.value.containsIgnoreCase(value))
                        )
        );
    }
}
