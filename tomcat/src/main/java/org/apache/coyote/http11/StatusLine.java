package org.apache.coyote.http11;

public record StatusLine(
        String version,
        int statusCode,
        String reasonPhrase
) {

    public String toLine() {
        return version + " " + statusCode + " " + reasonPhrase;
    }
}
