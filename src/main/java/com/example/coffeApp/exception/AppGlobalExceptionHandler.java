package com.example.coffeApp.exception;

import com.example.coffeApp.filter.LoggerDetail;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class AppGlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({AppException.class})
    public ResponseEntity<AppErrorResponse> handleAppException(AppException ex, WebRequest request) {
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;

        AppErrorResponse response = AppErrorResponse.builder()
                .message(ex.getMessage())
                .status(httpStatus.value())
                .build();
        writeExceptionLog(ex, request);
        return new ResponseEntity<>(response, new HttpHeaders(), httpStatus);
    }

    @ExceptionHandler({AppValidationException.class})
    public ResponseEntity<AppErrorResponse> handleAppValidationException(AppValidationException ex, WebRequest request) {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

        AppErrorResponse response = AppErrorResponse.builder()
                .message(ex.getMessage())
                .status(httpStatus.value())
                .build();
        writeExceptionLog(ex, request);
        return new ResponseEntity<>(response, new HttpHeaders(), httpStatus);
    }

    @ExceptionHandler({AppNotFoundException.class})
    public ResponseEntity<AppErrorResponse> handleAppNotFoundException(AppNotFoundException ex, WebRequest request) {
        HttpStatus httpStatus = HttpStatus.NOT_FOUND;

        AppErrorResponse response = AppErrorResponse.builder()
                .message(ex.getMessage())
                .status(httpStatus.value())
                .build();
        writeExceptionLog(ex, request);
        return new ResponseEntity<>(response, new HttpHeaders(), httpStatus);
    }

    @Override
    @NonNull
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  @NonNull HttpHeaders headers,
                                                                  @NonNull HttpStatus status,
                                                                  @NonNull WebRequest request) {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

        Map<String, Object> fields = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(fieldError ->
                fields.put(fieldError.getField(), fieldError.getDefaultMessage())
        );
        AppErrorResponse response = AppErrorResponse.builder()
                .message("Geçersiz parametre")
                .fields(fields)
                .status(httpStatus.value())
                .build();
        writeExceptionLog(ex, request);
        return new ResponseEntity<>(response, headers, status);
    }


    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<AppErrorResponse> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
        HttpStatus httpStatus = HttpStatus.FORBIDDEN;


        AppErrorResponse response = AppErrorResponse.builder()
                .message("Erişim Reddedildi")
                .status(httpStatus.value())
                .build();

        writeExceptionLog(ex, request);
        return new ResponseEntity<>(response, new HttpHeaders(), httpStatus);
    }


    @Override
    @NonNull
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex,
                                                                          @NonNull HttpHeaders headers,
                                                                          @NonNull HttpStatus status,
                                                                          @NonNull WebRequest request) {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

        Map<String, Object> fields = new HashMap<>();
        fields.put(ex.getParameterName(), "parametre zorunludur");
        AppErrorResponse response = AppErrorResponse.builder()
                .message("Zorunlu parametre")
                .fields(fields)
                .status(httpStatus.value())
                .build();

        writeExceptionLog(ex, request);
        return new ResponseEntity<>(response, headers, status);
    }

    @Override
    @NonNull
    protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex,
                                                        @NonNull HttpHeaders headers,
                                                        @NonNull HttpStatus status,
                                                        @NonNull WebRequest request) {

        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        Map<String, Object> fields = new HashMap<>();
        fields.put(((MethodArgumentTypeMismatchException) ex).getName(), "beklenen veri tipi " + Objects.requireNonNull(ex.getRequiredType()).getSimpleName());
        AppErrorResponse response = AppErrorResponse.builder()
                .message("Veri tipi uyumsuz")
                .fields(fields)
                .status(httpStatus.value())
                .build();

        writeExceptionLog(ex, request);
        return new ResponseEntity<>(response, headers, status);
    }

    @ExceptionHandler(CustomValidationException.class)
    public ResponseEntity<?> CustomValidationException(CustomValidationException exception, HttpServletRequest request) {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

        AppErrorResponse response = AppErrorResponse.builder()
                .message(exception.getMessage())
                .fields(exception.getData())
                .status(httpStatus.value())
                .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    private void writeExceptionLog(Exception ex, WebRequest request) {
        ServletWebRequest servletWebRequest = ((ServletWebRequest) request);
        HttpServletRequest httpServletRequest = servletWebRequest.getRequest();
        String requestMethod = httpServletRequest.getMethod();
        String requestStr = httpServletRequest.getRequestURI();
        String queryString = httpServletRequest.getQueryString();
        String queryClause = StringUtils.hasLength(queryString) ? "?" + queryString : "";
        String params = request.getParameterMap().entrySet().stream().map(entry -> entry.getKey() + ":" +
                Arrays.toString(entry.getValue())).collect(Collectors.joining(", "));

        String message = requestMethod + " " + requestStr + queryClause;

        LoggerDetail loggerDetail = new LoggerDetail();
        loggerDetail.getValues().put("requestURI", requestStr);
        loggerDetail.getValues().put("queryString", queryString);
        loggerDetail.getValues().put("requestMethod", requestMethod);
        loggerDetail.getValues().put("requestParams", params);

        if (ex.getClass().equals(MethodArgumentNotValidException.class)) {
            loggerDetail.getValues().put("ExceptionType", ex.getClass().getSimpleName());
            log.error(message + " FAILED", loggerDetail);
        } else {
            log.error(message + " FAILED", loggerDetail, ex);
        }
    }
}
