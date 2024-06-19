package no.jobbscraper.jobpostapi.exception;

import no.jobbscraper.jobpostapi.response.Response;
import no.jobbscraper.jobpostapi.response.ResponseErrorDto;
import no.jobbscraper.jobpostapi.response.ResponseUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {

    private final ResponseUtil responseUtil;

    public GlobalExceptionHandler(ResponseUtil responseUtil) {
        this.responseUtil = responseUtil;
    }
    
    @ExceptionHandler(value = BadSecretKeyException.class)
    public ResponseEntity<Response<ResponseErrorDto>> handleBadSecretKeyException(
            BadSecretKeyException exception
    ) {
        ResponseErrorDto errorDTO = responseUtil.createAPIErrorDTO(exception);
        Response<ResponseErrorDto> response = responseUtil.createAPIResponse(errorDTO);
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(value = JobPostNotFoundException.class)
    public ResponseEntity<Response<ResponseErrorDto>> handleJobPostNotFoundException(
            JobPostNotFoundException exception
    ) {
        ResponseErrorDto errorDTO = responseUtil.createAPIErrorDTO(exception);
        Response<ResponseErrorDto> response = responseUtil.createAPIResponse(errorDTO);
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(value = BindException.class)
    public ResponseEntity<Response<ResponseErrorDto>> handleBindException(
            BindException exception
    ) {
        List<ResponseErrorDto> errorDTOList = responseUtil.createAPIErrorDTOsForBindException(exception);
        Response<ResponseErrorDto> response = responseUtil.createAPIResponse(errorDTOList);
        return ResponseEntity.badRequest().body(response);
    }

}
