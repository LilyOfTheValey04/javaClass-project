package com.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.View;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final View error;
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    public GlobalExceptionHandler(View error) {
        this.error = error;
    }

    @ExceptionHandler(ResourceNotFound.class)
    public ProblemDetail handleNotFound(ResourceNotFound ex, HttpServletRequest req) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problemDetail.setInstance(URI.create(req.getRequestURI()));
        problemDetail.setTitle("Resource Not Found");
        return problemDetail;
    }

    @ExceptionHandler(InsufficientQuantityException.class)
    public ProblemDetail handleInsufficientQuantity(InsufficientQuantityException ex, HttpServletRequest req) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problemDetail.setInstance(URI.create(req.getRequestURI()));
        problemDetail.setTitle("Insufficient Quantity");
        if (ex.getBody() != null) {
            problemDetail.setProperty("details", ex.getBody().getProperties());
        }
        return problemDetail;
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleRequestObjectValidation(MethodArgumentNotValidException ex,
                                                       HttpServletRequest req){
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,"Validation failed");
        problemDetail.setInstance(URI.create(req.getRequestURI()));
        problemDetail.setTitle("Request object validation failed");

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach( error ->
                errors.put(error.getField(), error.getDefaultMessage()));

        return problemDetail;

    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleRequestPropertyValidation(ConstraintViolationException ex,
                                                         HttpServletRequest req){
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "One or more request validation failed");
        problemDetail.setInstance(URI.create(req.getRequestURI()));
        problemDetail.setTitle("Property validation failed");

        Map<String, String> errors = new HashMap<>();

        ex.getConstraintViolations().forEach(error ->
                errors.put(error.getPropertyPath().toString(), error.getMessage()));

        problemDetail.setProperty("error", errors);
        return problemDetail;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest req) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problemDetail.setInstance(URI.create(req.getRequestURI()));
        return problemDetail;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleAllException (Exception ex, HttpServletRequest req){
        ex.printStackTrace();
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Unexpected exception occurred");
        problemDetail.setInstance(URI.create(req.getRequestURI()));
        problemDetail.setTitle("Internal Server Error");
        return  problemDetail;
    }
}
