package org.apache.coyote.http11.response.headers;

public class ContentLength implements ResponseHeader {

    private static final long MIN_LENGTH = 0;

    private final long length;

    public ContentLength(long length) {
        this.length = Math.max(MIN_LENGTH, length);
    }

    public static ContentLength from(String bodyString) {
        return new ContentLength(bodyString.getBytes().length);
    }

    @Override
    public String getAsString() {
        return "Content-Length: " + length;
    }
}
