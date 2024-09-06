package org.apache.coyote.http11;

public class StatusLine {
    private final HttpVersion httpVersion;
    private final StatusCode statusCode;

    private StatusLine(HttpVersion httpVersion, StatusCode statusCode) {
        this.httpVersion = httpVersion;
        this.statusCode = statusCode;
    }

    public static StatusLine ok(HttpVersion httpVersion) {
        return new StatusLine(httpVersion, StatusCode.OK);
    }

    public static StatusLine found(HttpVersion httpVersion) {
        return new StatusLine(httpVersion, StatusCode.FOUND);
    }

    public static StatusLine unAuthorized(HttpVersion httpVersion) {
        return new StatusLine(httpVersion, StatusCode.UNAUTHORIZED);
    }

    public static StatusLine notFound(HttpVersion httpVersion) {
        return new StatusLine(httpVersion, StatusCode.NOT_FOUND);
    }

    public static StatusLine internalServerError(HttpVersion httpVersion) {
        return new StatusLine(httpVersion, StatusCode.INTERNAL_SERVER_ERROR);
    }

    @Override
    public String toString() {
        return httpVersion + " " + statusCode;
    }
}

