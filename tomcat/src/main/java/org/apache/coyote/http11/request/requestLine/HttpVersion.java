package org.apache.coyote.http11.request.requestLine;

public enum HttpVersion {
    HTTP_1_1,
    ;

    public static HttpVersion toHttpVersion(String httpVersion) {
        if (httpVersion.equals("HTTP/1.1")) {
            return HTTP_1_1;
        }
        throw new IllegalArgumentException("지원하지 않는 HTTP버전입니다.");
    }
}
