package org.apache.coyote.http11.response.headers;

import org.apache.coyote.http11.response.ResponseEntity;

import java.util.Objects;

public class ResponseHeaders {

    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String KEY_VALUE_DELIMITER = ": ";
    private static final String SPACE = " ";
    private static final String NEW_LINE = "\r\n";

    private final ContentType contentType;
    private final int contentLength;

    private ResponseHeaders(final ContentType contentType, final int contentLength) {
        this.contentType = contentType;
        this.contentLength = contentLength;
    }

    public static ResponseHeaders from(final ResponseEntity responseEntity) {
        return new ResponseHeaders(responseEntity.getContentType(), responseEntity.calculateContentLength());
    }

    public String convertToString() {
        return String.join(NEW_LINE,
                convertContentType(),
                convertContentLength()
        );
    }

    private String convertContentType() {
        return new StringBuilder().append(CONTENT_TYPE).append(KEY_VALUE_DELIMITER).append(contentType.convertToString()).append(SPACE)
                                  .toString();
    }

    private String convertContentLength() {
        return new StringBuilder().append(CONTENT_LENGTH).append(KEY_VALUE_DELIMITER).append(contentLength).append(SPACE)
                                  .toString();
    }

    public ContentType getContentType() {
        return contentType;
    }

    public int getContentLength() {
        return contentLength;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final ResponseHeaders that = (ResponseHeaders) o;
        return contentLength == that.contentLength && contentType == that.contentType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(contentType, contentLength);
    }

    @Override
    public String toString() {
        return "ResponseHeaders{" +
                "contentType=" + contentType +
                ", contentLength=" + contentLength +
                '}';
    }
}
