package no.jobbscraper.jobpostapi.exception;

public class JobPostNotFoundException extends RuntimeException {

    public JobPostNotFoundException(Long id) {
        super("Job post with id " + id + " was not found");
    }
}
