package org.apache.coyote.model.response;

public class HttpStatusCode {

    private static final String version = "HTTP/1.1 ";
    private final StatusCode statusCode;

    private HttpStatusCode(final StatusCode statusCode) {
        this.statusCode = statusCode;
    }

    public static HttpStatusCode of(final StatusCode statusCode) {
        return new HttpStatusCode(statusCode);
    }

    public String getResponse() {
        return version + statusCode.getStatusCode() + " " + statusCode.name() + " ";
    }
}
