package org.apache.coyote.http11;

public enum HttpProtocol {
    HTTP11,
    ;

    public static HttpProtocol from(String protocol) {
        if ("HTTP/1.1".equals(protocol)) {
            return HTTP11;
        }
        throw new IllegalArgumentException("지원되지 않는 HTTP 프로토콜 입니다.");
    }
}
