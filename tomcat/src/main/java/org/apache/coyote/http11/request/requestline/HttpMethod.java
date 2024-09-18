package org.apache.coyote.http11.request.requestline;

public enum HttpMethod {
    GET,
    POST,
    DELETE;

    public static HttpMethod from(String data) {
        if ("GET".equals(data.toUpperCase().strip())) {
            return GET;
        }
        if ("POST".equals(data.toUpperCase().strip())) {
            return POST;
        }
        if ("DELETE".equals(data.toUpperCase().strip())) {
            return DELETE;
        }
        throw new IllegalArgumentException("%s는 존재하지 않는 HTTP 메서드입니다.");
    }
}
