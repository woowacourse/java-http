package org.apache.coyote.http11;

public class ContentLength {

    public static final ContentLength EMPTY = new ContentLength(0);

    private final int length;

    private ContentLength(final int length) {
        this.length = length;
    }

    public static ContentLength from(String body) {
        int bodyLength = body.getBytes().length;
        return new ContentLength(bodyLength);
    }

    @Override
    public String toString() {
        return "Content-Length: " + length;
    }

}
