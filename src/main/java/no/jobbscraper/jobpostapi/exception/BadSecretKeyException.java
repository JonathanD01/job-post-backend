package no.jobbscraper.jobpostapi.exception;

public class BadSecretKeyException extends RuntimeException {

    public BadSecretKeyException() {
        super("Invalid access");
    }
}
