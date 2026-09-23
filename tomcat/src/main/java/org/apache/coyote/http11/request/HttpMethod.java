package org.apache.coyote.http11.request;

public enum HttpMethod {

    OPTIONS,
    GET,
    HEAD,
    POST,
    PUT,
    DELETE,
    TRACE,
    CONNECT;

    public static HttpMethod from(final String httpMethod) {
        if (httpMethod == null) {
            throw new IllegalArgumentException("HTTP 메서드가 없습니다.");
        }

        try {
            return HttpMethod.valueOf(httpMethod);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("지원하지 않는 HTTP 메서드: " + httpMethod, e);
        }
    }

}
