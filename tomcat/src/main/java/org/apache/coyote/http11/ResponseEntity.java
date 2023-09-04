package org.apache.coyote.http11;

public class ResponseEntity<T> {

    private HttpStatusCode statusCode;
    private final T responseBody;

    public ResponseEntity(final T responseBody) {
        this.responseBody = responseBody;
    }

    public ResponseEntity<T> ok() {
        statusCode = HttpStatusCode.OK;
        return this;
    }

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }

    public T getResponseBody() {
        return responseBody;
    }
}
