package no.jobbscraper.jobpostapi.response;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;

public class ResponseUtil {

    /**
     * Builds a success API response with the given result.
     *
     * @param result    The result to be included in the response.
     * @param <T>       The type of the result.
     * @return          A Response object representing a successful response with the given result.
     */
    public <T> Response<T> buildSuccessResponse(T result) {
        return new Response.Builder<T>()
                .setType(ResponseType.SUCCESS)
                .setResult(result)
                .build();
    }

    /**
     * Creates a ResponseErrorDto object based on the provided exception.
     *
     * @param exception The exception from which to create the ResponseErrorDto.
     * @return          The created ResponseErrorDto object.
     */
    public ResponseErrorDto createAPIErrorDTO(Exception exception) {
        return new ResponseErrorDto(exception.getMessage(), HttpStatus.BAD_REQUEST, ZonedDateTime.now(ZoneId.of("Z")));
    }

    /**
     * Creates a List of ResponseErrorDto object based on the provided exception.
     *
     * @param bindException The exception from which to create the APIErrorDTOs.
     * @return              The created List of APIErrorDTOs object.
     */
    public List<ResponseErrorDto> createAPIErrorDTOsForBindException(BindException bindException) {
        return bindException.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new ResponseErrorDto(error.getDefaultMessage(), HttpStatus.BAD_REQUEST,
                        ZonedDateTime.now(ZoneId.of("Z"))))
                .collect(Collectors.toList());
    }

    /**
     * Creates a Response object with the List of ResponseErrorDto objects.
     *
     * @param errorDTOList  The List of APIErrorDTOs to include in the Response.
     * @return              The created Response object.
     */
    public Response<ResponseErrorDto> createAPIResponse(List<ResponseErrorDto> errorDTOList) {
        return new Response.Builder<ResponseErrorDto>()
                .setType(ResponseType.FAILED)
                .setErrors(errorDTOList)
                .build();
    }

    /**
     * Creates a Response object with the ResponseErrorDto object.
     *
     * @param errorDTO  The ResponseErrorDto to include in the Response.
     * @return          The created Response object.
     */
    public Response<ResponseErrorDto> createAPIResponse(ResponseErrorDto errorDTO) {
        return new Response.Builder<ResponseErrorDto>()
                .setType(ResponseType.FAILED)
                .setErrors(Collections.singletonList(errorDTO))
                .build();
    }
}
