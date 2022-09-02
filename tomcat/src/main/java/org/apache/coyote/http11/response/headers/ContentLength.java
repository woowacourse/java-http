package org.apache.coyote.http11.response.headers;

public class ContentLength implements ResponseHeader {

    private static final int MIN_LENGTH = 0;

    private final int length;

    private ContentLength(int length) {
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
