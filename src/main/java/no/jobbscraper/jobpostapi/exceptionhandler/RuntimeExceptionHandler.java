package no.jobbscraper.jobpostapi.exceptionhandler;

import no.jobbscraper.jobpostapi.response.Response;
import no.jobbscraper.jobpostapi.response.ResponseErrorDto;
import no.jobbscraper.jobpostapi.response.ResponseUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class RuntimeExceptionHandler {

    private final ResponseUtil responseUtil;

    public RuntimeExceptionHandler(ResponseUtil responseUtil) {
        this.responseUtil = responseUtil;
    }

    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<Response<ResponseErrorDto>> handleException(
            RuntimeException exception
    ) {
        ResponseErrorDto errorDTO = responseUtil.createAPIErrorDTO(exception);
        Response<ResponseErrorDto> response = responseUtil.createAPIResponse(errorDTO);
        return ResponseEntity.badRequest().body(response);
    }

}
