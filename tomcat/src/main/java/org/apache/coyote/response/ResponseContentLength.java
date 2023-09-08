package org.apache.coyote.response;

public class ResponseContentLength {
    private final int contentLength;

    public ResponseContentLength(final int contentLength) {
        this.contentLength = contentLength;
    }

    @Override
    public String toString() {
        return "Content-Length: " + contentLength;
    }
}
