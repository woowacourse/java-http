package org.apache.coyote.http11;

public record HttpStatusLine(
        String httpVersion,
        int statusCode,
        String reason
) {
    @Override
    public String toString() {
        return httpVersion + " " + statusCode + " " + reason;
    }
}
