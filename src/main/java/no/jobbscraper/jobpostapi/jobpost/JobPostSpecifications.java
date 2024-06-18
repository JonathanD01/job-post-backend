package no.jobbscraper.jobpostapi.jobpost;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import no.jobbscraper.jobpostapi.jobdefinition.JobDefinition;
import no.jobbscraper.jobpostapi.util.GeoUtil;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class JobPostSpecifications {

    /**
     * Specifies a condition where the {@link JobPost} must have a non-null URL.
     *
     * @return A Specification object representing the condition.
     * @see JobPost
     */
    public static Specification<JobPost> hasUrl() {
        return (root, query, cb) -> cb.isNotNull(root.get("url"));
    }

    /**
     * Specifies a condition where the {@link JobPost} deadline has not passed and is considered valid.
     *
     * @return A Specification object representing the condition.
     * @see JobPost
     */
    public static Specification<JobPost> hasDeadlineNotPassed() {
        return (root, query, cb) -> {
            Predicate deadlinePredicate = cb.or(
                    cb.greaterThanOrEqualTo(root.get("deadline"), LocalDate.now()),
                    cb.isNull(root.get("deadline")));

            List<Order> orderList = new ArrayList<>();
            orderList.add(cb.asc(root.get("deadline")));
            orderList.add(cb.desc(root.get("createdAt")));

            Order[] orders = orderList.toArray(new Order[0]);

            return query
                    .orderBy(orders)
                    .where(deadlinePredicate)
                    .getRestriction();
        };
    }

    /**
     * Specifies a condition where the {@link JobPost} position matches the specified position.
     *
     * @param position  The position to match.
     * @return          A Specification object representing the condition.
     * @see JobPost
     */
    public static Specification<JobPost> hasPosition(String position) {
        return (root, query, cb) -> {
            Join<JobPost, JobDefinition> joinJobDescription = root.join("jobDefinitions");

            Predicate stillingPredicate = cb.equal(joinJobDescription.get("key"), "Stilling");
            Predicate valuePredicate = cb.like(cb.lower(joinJobDescription.get("value")), "%" + position.toLowerCase() + "%");

            return cb.and(stillingPredicate, valuePredicate);
        };
    }

    /**
     * Specifies a condition where the {@link JobPost} description contains the specified text.
     *
     * @param description The text to search for in the job post's description.
     * @return A Specification object representing the condition.
     * @see JobPost
     */
    public static Specification<JobPost> hasDescription(String description) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("description")),"%" + description.toLowerCase() + "%");
    }

    /**
     * Specifies a condition where the {@link JobPost} is located in one or more municipalities.
     *
     * @param inputMunicipality     A string containing one or more municipalities separated by commas.
     * @return                      A Specification object representing the condition.
     * @throws NullPointerException if the list of words for the current municipality is null
     * @see JobPost
     */
    public static Specification<JobPost> isInMunicipality(String inputMunicipality) {
        return (root, query, cb) -> {
            // Joining the jobDefinitions association
            Join<JobPost, JobDefinition> joinJobDescriptions = root.join("jobDefinitions");

            // Split the inputMunicipality string into separate municipalities
            String[] allMunicipalities = inputMunicipality.split(",");

            // Constructing the predicates for each municipality
            Predicate[] allMunicipalityPredicates = new Predicate[allMunicipalities.length];
            for (int i = 0; i < allMunicipalities.length; i++) {
                String municipality = allMunicipalities[i].toLowerCase();

                // Get the list of words for the current municipality
                // TODO troms og finmark returns null
                // TODO get or default
                List<String> municipalityWords = GeoUtil.municipalityCityMap.get(municipality);
                if (municipalityWords == null) {
                    throw new NullPointerException("Municipality: " + municipality + " returned null from GeoUtil");
                }

                // Constructing the predicates for each word in the municipality
                Predicate[] valuePredicates = new Predicate[municipalityWords.size()];
                for (int j = 0; j < municipalityWords.size(); j++) {
                    String municipalityWord = "%" + municipalityWords.get(j).toLowerCase() + "%";
                    valuePredicates[j] = cb.like(cb.lower(joinJobDescriptions.get("value")), municipalityWord);
                }

                Predicate finalValuePredicate = cb.or(valuePredicates);

                Predicate keyPredicate = cb.equal(joinJobDescriptions.get("key"), "Sted");
                Predicate municipalityPredicate = cb.and(keyPredicate, finalValuePredicate);

                allMunicipalityPredicates[i] = cb.or(municipalityPredicate);
            }
            return cb.or(allMunicipalityPredicates);
        };
    }

    /**
     * Specifies a condition where the {@link JobPost} belongs to the specified sector.
     *
     * @param sector    The sector to match.
     * @return          A Specification object representing the condition.
     * @see JobPost
     */
    public static Specification<JobPost> hasSector(String sector) {
        return (root, query, cb) -> {
            Join<JobPost, JobDefinition> joinJobDescription = root.join("jobDefinitions");

            String finalSectorValue = sector.toLowerCase();

            Predicate hasSectorKey = cb.equal(joinJobDescription.get("key"), "Sektor");
            Predicate hasSectorValue = cb.equal(cb.lower(joinJobDescription.get("value")), finalSectorValue);

            return cb.and(hasSectorKey, hasSectorValue);
        };
    }

    /**
     * Specifies a condition where the {@link JobPost} has the specified deadline type.
     *
     * @param deadline  The deadline type ("nærmest" or "lengst unna").
     * @return          A Specification object representing the condition.
     * @see JobPost
     */
    public static Specification<JobPost> hasDeadline(String deadline) {
        return (root, query, cb) -> {
            Predicate hasDeadline = cb.isNotNull(root.get("deadline"));
            Predicate doesNotHaveDeadline = cb.isNull(root.get("deadline"));
            Predicate deadlineNotPassed = cb.greaterThanOrEqualTo(root.get("deadline"), LocalDate.now());

            return switch (deadline.toLowerCase()) {
                case "nærmest" -> query.orderBy(cb.asc(root.get("deadline")))
                        .where(deadlineNotPassed, hasDeadline)
                        .getRestriction();
                case "lengst unna" -> query.orderBy(cb.desc(root.get("deadline")))
                        .where(hasDeadline)
                        .getRestriction();
                default -> doesNotHaveDeadline;
            };
        };
    }
}
