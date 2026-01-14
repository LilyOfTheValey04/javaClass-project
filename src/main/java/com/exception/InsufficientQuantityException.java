package com.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponse;

public class InsufficientQuantityException extends RuntimeException implements ErrorResponse {
    private final ProblemDetail body;
    private final HttpStatus status;

    public InsufficientQuantityException(Class<?> resource, int requestedQuantity) {
        super("Insufficient quantity for " + resource);

        this.status = HttpStatus.BAD_REQUEST;

        ProblemDetail pd = ProblemDetail.forStatus(status);
        pd.setTitle("Insufficient Quantity");
        pd.setDetail("Requested quantity: " + requestedQuantity + " exceeds available quantity.");
        pd.setProperty("requestedQuantity", requestedQuantity);

        this.body = pd;
    }

    @Override
    public HttpStatus getStatusCode() {
        return status;
    }

    @Override
    public ProblemDetail getBody() {
        return body;
    }
}
