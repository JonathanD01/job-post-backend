package no.jobbscraper.jobpostapi.exceptionhandler;

import no.jobbscraper.jobpostapi.response.Response;
import no.jobbscraper.jobpostapi.response.ResponseErrorDto;
import no.jobbscraper.jobpostapi.response.ResponseUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;

@ControllerAdvice
public class BindExceptionHandler {

    private final ResponseUtil responseUtil;

    public BindExceptionHandler(ResponseUtil responseUtil) {
        this.responseUtil = responseUtil;
    }

    @ExceptionHandler(value = BindException.class)
    public ResponseEntity<Response<ResponseErrorDto>> handleException(
            BindException exception
    ) {
        List<ResponseErrorDto> errorDTOList = responseUtil.createAPIErrorDTOsForBindException(exception);
        Response<ResponseErrorDto> response = responseUtil.createAPIResponse(errorDTOList);
        return ResponseEntity.badRequest().body(response);
    }

}
