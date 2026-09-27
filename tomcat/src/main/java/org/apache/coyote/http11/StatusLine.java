package org.apache.coyote.http11;

public record StatusLine(
    HttpVersion version,
    HttpStatus status
) {

    public static StatusLine http11(HttpStatus status) {
        return new StatusLine(HttpVersion.VERSION_11, status);
    }

    @Override
    public String toString() {
        return String.join(" ",
            version.getName(),
            status.toString() + " ");
    }
}
