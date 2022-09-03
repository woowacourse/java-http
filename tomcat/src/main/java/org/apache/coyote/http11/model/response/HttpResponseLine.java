package org.apache.coyote.http11.model.response;

public class HttpResponseLine {
    private static final String version = "HTTP/1.1 ";
    private final HttpStatusCode statusCode;

    private HttpResponseLine(final HttpStatusCode statusCode) {
        this.statusCode = statusCode;
    }

    public static HttpResponseLine of(final HttpStatusCode statusCode) {
        return new HttpResponseLine(statusCode);
    }

    public String getResponseLine() {
        return version + statusCode.getStatusCode() + " " +statusCode.name()  + " ";
    }
}
