package org.apache.coyote.model.response;

public class ResponseLine {

    private static final String version = "HTTP/1.1 ";
    private final StatusCode statusCode;

    private ResponseLine(final StatusCode statusCode) {
        this.statusCode = statusCode;
    }

    public static ResponseLine of(final StatusCode statusCode) {
        return new ResponseLine(statusCode);
    }

    public String getResponse() {
        return version + statusCode.getStatusCode() + " " + statusCode.name() + " ";
    }
}
