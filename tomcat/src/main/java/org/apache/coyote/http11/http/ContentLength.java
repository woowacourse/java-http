package org.apache.coyote.http11.http;

public class ContentLength {

    private final int contentLength;

    private ContentLength(final int contentLength) {
        this.contentLength = contentLength;
    }

    public static ContentLength from(final String contentLength) {
        final String parsedContentLength = contentLength.replaceAll(" ", "");

        try {
            return new ContentLength(Integer.parseInt(parsedContentLength));
        } catch (NumberFormatException e) {
            throw new NumberFormatException("content-Length 는 숫자로 생성돼야 합니다.");
        }
    }

    public int getContentLength() {
        return contentLength;
    }
}
