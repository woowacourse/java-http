package org.apache.coyote.http11;

public class ContentLength {

    private final int contentLength;

    public ContentLength(final int contentLength) {
        this.contentLength = contentLength;
    }

    public static ContentLength from(final String contentLength) {
        try {
            return new ContentLength(Integer.parseInt(contentLength));
        } catch (NumberFormatException e) {
            throw new NumberFormatException("content-Length 는 숫자로 생성돼야 합니다.");
        }
    }

    public int getContentLength() {
        return contentLength;
    }
}
