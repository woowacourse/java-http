package org.apache.coyote.response;

public class ResponseContentType {

    private final String contentType;

    public ResponseContentType(final String contentType) {
        this.contentType = contentType;
    }

    @Override
    public String toString() {
        return "Content-Type: " + contentType;
    }
}
