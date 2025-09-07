package org.apache.coyote.http11.handler;

public record HandlerResult(
        String status,
        String contentType,
        byte[] body
) {

    public final static String DEFAULT_CONTENT_TYPE = "text/plain";
    public final static String DEFAULT_ENCODING_TYPE = "utf-8";

    public static HandlerResult ok(final String contentType, final byte[] body) {
        final String contentTypeValue = createContentTypeValue(contentType, DEFAULT_ENCODING_TYPE);
        return new HandlerResult("200 OK", contentTypeValue, body);
    }

    public static HandlerResult found(final String contentType, final byte[] body) {
        final String contentTypeValue = createContentTypeValue(contentType, DEFAULT_ENCODING_TYPE);
        return new HandlerResult("302 Found", contentTypeValue, body);
    }

    public static HandlerResult unauthorized(final String contentType, final byte[] body) {
        final String contentTypeValue = createContentTypeValue(contentType, DEFAULT_ENCODING_TYPE);
        return new HandlerResult("401 Unauthorized", contentTypeValue, body);
    }

    public static HandlerResult notFound(final String message) {
        final String contentTypeValue = createContentTypeValue(DEFAULT_CONTENT_TYPE, DEFAULT_ENCODING_TYPE);
        return new HandlerResult("404 Not Found", contentTypeValue, message.getBytes());
    }

    public static HandlerResult notFound(final String contentType, final byte[] body) {
        final String contentTypeValue = createContentTypeValue(contentType, DEFAULT_ENCODING_TYPE);
        return new HandlerResult("404 Not Found", contentTypeValue, body);
    }

    public static HandlerResult serverError(final String message) {
        final String contentTypeValue = createContentTypeValue(DEFAULT_CONTENT_TYPE, DEFAULT_ENCODING_TYPE);
        return new HandlerResult("500 Internal Server Error", contentTypeValue, message.getBytes());
    }

    public static HandlerResult serverError(final String contentType, final byte[] body) {
        final String contentTypeValue = createContentTypeValue(contentType, DEFAULT_ENCODING_TYPE);
        return new HandlerResult("500 Internal Server Error", contentTypeValue, body);
    }

    private static String createContentTypeValue(final String contentType, final String encodingType) {
        return String.format("%s;charset=%s", contentType, encodingType);
    }
}
