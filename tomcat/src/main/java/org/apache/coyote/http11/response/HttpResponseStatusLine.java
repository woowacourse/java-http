package org.apache.coyote.http11.response;

public record HttpResponseStatusLine(
        String version,
        HttpStatus status
) {
    public static HttpResponseStatusLine from(HttpStatus status) {
        return new HttpResponseStatusLine("HTTP/1.1", status);
    }

    public String format() {
        return version + " " + status.status() + " ";
    }
}
