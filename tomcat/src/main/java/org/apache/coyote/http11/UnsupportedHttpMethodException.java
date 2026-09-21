package org.apache.coyote.http11;

final class UnsupportedHttpMethodException extends HttpRequestParseException {

    UnsupportedHttpMethodException(String method) {
        super("지원하지 않는 HTTP 메서드입니다: " + method);
    }
}
