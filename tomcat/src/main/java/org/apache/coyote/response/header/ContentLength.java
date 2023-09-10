package org.apache.coyote.response.header;

public class ContentLength {
    private final int length;

    public ContentLength(final int length) {
        this.length = length;
    }

    @Override
    public String toString() {
        return "Content-Length: " + length;
    }
}
