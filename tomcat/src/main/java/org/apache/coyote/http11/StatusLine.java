package org.apache.coyote.http11;

public record StatusLine(
        HttpVersion version,
        HttpStatus status
) {

    public String serialize() {
        return version.value() + " " + status.value();
    }
}
