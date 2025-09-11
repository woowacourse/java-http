package org.apache.coyote.http11;

public final class StatusLine {

    private final HttpVersion httpVersion;
    private final int statusCode;
    private final String reasonPhrase;

    private StatusLine(final HttpVersion httpVersion, final int statusCode, final String reasonPhrase) {
        this.httpVersion = httpVersion;
        this.statusCode = statusCode;
        this.reasonPhrase = reasonPhrase;
    }

    public static StatusLine from(final int statusCode) {
        final HttpVersion version = HttpVersion.HTTP_1_1;
        final String reason = getReasonPhrase(statusCode);
        return new StatusLine(version, statusCode, reason);
    }

    public String toLineString() {
        return String.join(" ", httpVersion.getText(), String.valueOf(statusCode), reasonPhrase);
    }

    private static String getReasonPhrase(final int code) {
        return switch (code) {
            case 200 -> "OK";
            case 201 -> "Created";
            case 302 -> "Found";
            case 400 -> "Bad Request";
            case 401 -> "Unauthorized";
            case 403 -> "Forbidden";
            case 404 -> "Not Found";
            case 500 -> "Internal Server Error";
            default -> "";
        };
    }
}
