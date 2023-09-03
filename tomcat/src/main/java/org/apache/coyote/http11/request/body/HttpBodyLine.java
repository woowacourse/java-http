package org.apache.coyote.http11.request.body;

public class HttpBodyLine {
    private final String body;

    private HttpBodyLine(final String body) {
        this.body = body;
    }

    public static HttpBodyLine from(final String httpBodyLine) {
        return new HttpBodyLine(httpBodyLine);
    }
}
