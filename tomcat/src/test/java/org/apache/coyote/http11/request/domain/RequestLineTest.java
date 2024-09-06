package org.apache.coyote.http11.request.domain;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestLineTest {

    @Test
    @DisplayName("경로가 / 인 경우 true를 반환한다.")
    void isDefaultPath() {
        RequestLine requestLine = new RequestLine("GET / HTTP/1.1");

        assertTrue(requestLine.isDefaultPath());
    }

    @Test
    @DisplayName("같은 RequestMethod인지 확인한다.")
    void isSameMethod() {
        RequestLine requestLine = new RequestLine("GET /index.html HTTP/1.1");

        assertAll(
                () -> assertTrue(requestLine.isSameMethod(RequestMethod.GET)),
                () -> assertFalse(requestLine.isSameMethod(RequestMethod.POST))
        );

    }

    @Test
    @DisplayName("특정 문자열로 시작하는지 확인한다.")
    void isStartsWith() {
        RequestLine requestLine = new RequestLine("GET /login?account=gugu&password=password HTTP/1.1");

        assertAll(
                () -> assertTrue(requestLine.isStartsWith("/login")),
                () -> assertFalse(requestLine.isStartsWith("/index"))
        );
    }
}
