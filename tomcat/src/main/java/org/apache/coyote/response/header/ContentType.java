package org.apache.coyote.response.header;

public class ContentType {

    private final String type;

    public ContentType(final String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Content-Type: " + type;
    }
}
