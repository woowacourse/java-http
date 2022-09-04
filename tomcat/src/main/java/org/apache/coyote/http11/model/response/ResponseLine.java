package org.apache.coyote.http11.model.response;

public class ResponseLine {

    private static final String version = "HTTP/1.1 ";

    private final ResponseStatusCode statusCode;

    private ResponseLine(final ResponseStatusCode statusCode) {
        this.statusCode = statusCode;
    }

    public static ResponseLine of(final ResponseStatusCode statusCode) {
        return new ResponseLine(statusCode);
    }

    public String getResponseLine() {
        return version + statusCode.getStatusCode() + " " +statusCode.name()  + " ";
    }
}
