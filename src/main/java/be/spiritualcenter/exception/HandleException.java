package be.spiritualcenter.exception;

/*
 * @author Raphael Zolotarev
 * @version 1.0
 * @license Copyright (c) 2025 www.zolotarev.eu
 * @since 03/03/2025
 */

import be.spiritualcenter.domain.HttpResponse;
import com.auth0.jwt.exceptions.JWTDecodeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import java.nio.file.AccessDeniedException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestControllerAdvice
@Slf4j
public class HandleException extends ResponseEntityExceptionHandler implements ErrorController {
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception exception, Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
        log.error(exception.getMessage());
        return new ResponseEntity<>(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .reason(exception.getMessage())
                        .devMessage(exception.getMessage())
                        .status(resolve(statusCode.value()))
                        .statusCode(statusCode.value())
                        .build(), statusCode);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
        log.error(exception.getMessage());
        List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors();
        String fieldMessage = fieldErrors.stream().map(FieldError::getDefaultMessage).collect(Collectors.joining(", "));
        return new ResponseEntity<>(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .reason(fieldMessage)
                        .devMessage(exception.getMessage())
                        .status(resolve(statusCode.value()))
                        .statusCode(statusCode.value())
                        .build(), statusCode);
    }

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<HttpResponse> sQLIntegrityConstraintViolationException(SQLIntegrityConstraintViolationException exception) {
        log.error(exception.getMessage());
        return new ResponseEntity<>(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .reason(exception.getMessage().contains("Duplicate entry") ? "Information already exists" : exception.getMessage())
                        .devMessage(exception.getMessage())
                        .status(BAD_REQUEST)
                        .statusCode(BAD_REQUEST.value())
                        .build(), BAD_REQUEST);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<HttpResponse> badCredentialsException(BadCredentialsException exception) {
        log.error(exception.getMessage());
        return new ResponseEntity<>(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .reason(exception.getMessage() + ", Incorrect email or password")
                        .devMessage(exception.getMessage())
                        .status(BAD_REQUEST)
                        .statusCode(BAD_REQUEST.value())
                        .build(), BAD_REQUEST);
    }

    @ExceptionHandler(APIException.class)
    public ResponseEntity<HttpResponse> apiException(APIException exception) {
        log.error(exception.getMessage());
        return new ResponseEntity<>(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .reason(exception.getMessage())
                        .devMessage(exception.getMessage())
                        .status(BAD_REQUEST)
                        .statusCode(BAD_REQUEST.value())
                        .build(), BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<HttpResponse> accessDeniedException(AccessDeniedException exception) {
        log.error(exception.getMessage());
        return new ResponseEntity<>(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .reason("Access denied. You don\'t have access")
                        .devMessage(exception.getMessage())
                        .status(FORBIDDEN)
                        .statusCode(FORBIDDEN.value())
                        .build(), FORBIDDEN);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<HttpResponse> exception(Exception exception) {
        log.error(exception.getMessage());
        System.out.println(exception);
        return new ResponseEntity<>(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .reason(exception.getMessage() != null ?
                                (exception.getMessage().contains("expected 1, actual 0") ? "Record not found" : exception.getMessage())
                                : "Some error occurred")
                        .devMessage(exception.getMessage())
                        .status(INTERNAL_SERVER_ERROR)
                        .statusCode(INTERNAL_SERVER_ERROR.value())
                        .build(), INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(JWTDecodeException.class)
    public ResponseEntity<HttpResponse> exception(JWTDecodeException exception) {
        log.error(exception.getMessage());
        return new ResponseEntity<>(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .reason("Could not decode the token")
                        .devMessage(exception.getMessage())
                        .status(INTERNAL_SERVER_ERROR)
                        .statusCode(INTERNAL_SERVER_ERROR.value())
                        .build(), INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(EmptyResultDataAccessException.class)
    public ResponseEntity<HttpResponse> emptyResultDataAccessException(EmptyResultDataAccessException exception) {
        log.error(exception.getMessage());
        return new ResponseEntity<>(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .reason(exception.getMessage().contains("expected 1, actual 0") ? "Record not found" : exception.getMessage())
                        .devMessage(exception.getMessage())
                        .status(BAD_REQUEST)
                        .statusCode(BAD_REQUEST.value())
                        .build(), BAD_REQUEST);
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<HttpResponse> disabledException(DisabledException exception) {
        log.error(exception.getMessage());
        return new ResponseEntity<>(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .devMessage(exception.getMessage())
                        .reason("User account is currently disabled")
                        .status(BAD_REQUEST)
                        .statusCode(BAD_REQUEST.value()).build()
                , BAD_REQUEST);
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<HttpResponse> lockedException(LockedException exception) {
        log.error(exception.getMessage());
        return new ResponseEntity<>(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .devMessage(exception.getMessage())
                        .reason("User account is currently locked")
                        .status(BAD_REQUEST)
                        .statusCode(BAD_REQUEST.value()).build()
                , BAD_REQUEST);
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<HttpResponse> dataAccessException(DataAccessException exception) {
        log.error(exception.getMessage());
        return new ResponseEntity<>(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .reason(processErrorMessage(exception.getMessage()))
                        .devMessage(processErrorMessage(exception.getMessage()))
                        .status(BAD_REQUEST)
                        .statusCode(BAD_REQUEST.value()).build()
                , BAD_REQUEST);
    }

    private ResponseEntity<HttpResponse> createErrorHttpResponse(HttpStatus httpStatus, String reason, Exception exception) {
        return new ResponseEntity<>(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .devMessage(exception.getMessage())
                        .reason(reason)
                        .status(httpStatus)
                        .statusCode(httpStatus.value()).build()
                , httpStatus);
    }

    private String processErrorMessage(String errorMessage) {
        if(errorMessage != null) {
            if(errorMessage.contains("Duplicate entry") && errorMessage.contains("AccountVerifications")) {
                return "You already verified your account.";
            }
            if(errorMessage.contains("Duplicate entry") && errorMessage.contains("ResetPasswordVerifications")) {
                return "We already sent you an email to reset your password.";
            }
            if(errorMessage.contains("Duplicate entry")) {
                return "Duplicate entry. Please try again.";
            }
        }
        return "Some error occurred";
    }
}