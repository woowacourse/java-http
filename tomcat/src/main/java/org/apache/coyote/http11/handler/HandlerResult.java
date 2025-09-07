package org.apache.coyote.http11.handler;

import java.util.HashMap;
import java.util.LinkedHashMap;
import org.apache.coyote.http11.dto.HttpCookie;

public record HandlerResult(
        Status status,
        String mimeType,
        String mimeParameter,
        HashMap<String, String> headers,
        HttpCookie httpCookie,
        byte[] body
) {

    public final static String DEFAULT_MIME_TYPE = "text/plain";
    public final static String DEFAULT_MIME_PARAMETER = "charset=utf-8";

    // == 팩토리 메서드 ==
    public static HandlerResult of(final Status status, final String contentType, final byte[] body) {
        return new HandlerResult(status, contentType, DEFAULT_MIME_PARAMETER, new LinkedHashMap<>(),
                new HttpCookie(new LinkedHashMap<>()), body);
    }

    public static HandlerResult text(final Status status, final String message) {
        return of(status, DEFAULT_MIME_TYPE, message.getBytes());
    }

    // == 응답 생성 메서드 ==
    public static HandlerResult ok(final String contentType, final byte[] body) {
        return of(Status.OK, contentType, body);
    }

    public static HandlerResult redirectFound(final String contentType, final byte[] body) {
        return of(Status.FOUND, contentType, body);
    }

    public static HandlerResult unauthorized(final String contentType, final byte[] body) {
        return of(Status.UNAUTHORIZED, contentType, body);
    }

    public static HandlerResult notFound(final String contentType, final byte[] body) {
        return of(Status.NOT_FOUND, contentType, body);
    }

    public static HandlerResult serverError(final String contentType, final byte[] body) {
        return of(Status.INTERNAL_ERROR, contentType, body);
    }

    // == 텍스트 기반 응답 생성 메서드 ==
    public static HandlerResult notFound(final String message) {
        return text(Status.NOT_FOUND, message);
    }

    public static HandlerResult serverError(final String message) {
        return text(Status.INTERNAL_ERROR, message);
    }

    // == 추가 메서드 ==
    public void addHeader(final String name, final String value) {
        this.headers.put(name, value);
    }
}
