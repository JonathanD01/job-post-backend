package no.jobbscraper.jobpostapi.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class Response<T> {

    @JsonProperty("response")
    private final ResponseType type;
    @JsonProperty("errors")
    private final List<ResponseErrorDto> errors;
    @JsonProperty("result")
    private final T result;

    private Response(ResponseType type, List<ResponseErrorDto> errors, T result) {
        this.type = type;
        this.errors = errors;
        this.result = result;
    }

    public ResponseType getType() {
        return type;
    }

    public List<ResponseErrorDto> getErrors() {
        return errors;
    }

    public T getResult() {
        return result;
    }

    public static class Builder<T> {
        private ResponseType type;
        private List<ResponseErrorDto> errors;
        private T result;

        public Builder(){}

        public Builder<T> setType(ResponseType type) {
            this.type = type;
            return this;
        }

        public Builder<T> setErrors(List<ResponseErrorDto> errors) {
            this.errors = errors;
            return this;
        }

        public Builder<T> setResult(T result) {
            this.result = result;
            return this;
        }

        public Response<T> build() {
            return new Response<>(type, errors, result);
        }
    }
}