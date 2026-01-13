package com;

import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponse;

public class ResourceNotFound extends RuntimeException implements ErrorResponse {
    private final ProblemDetail body;
    private final HttpStatus status;

    public ResourceNotFound(Class<?> resource, Object id) {
        super(extractResourceName(resource) + " with id=" + id + " was not found");
        this.status = HttpStatus.NOT_FOUND;

        ProblemDetail pd = ProblemDetail.forStatus(status);
        pd.setTitle("Resource Not Found");
        pd.setDetail(getMessage());
        pd.setProperty("resource", extractResourceName(resource));
        pd.setProperty("id", id);
        this.body = pd;
    }

    @Override
    @NonNull
    public HttpStatusCode getStatusCode() {
        return status;
    }

    @Override
    @NonNull
    public ProblemDetail getBody() {
        return body;
    }

    private static String extractResourceName(Class<?> resource) {
        return resource == null ? "Resource" : resource.getSimpleName();
    }

}
