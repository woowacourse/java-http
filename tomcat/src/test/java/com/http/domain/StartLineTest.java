package com.http.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class StartLineTest {

    @Test
    void 정상적인_스타트라인_파싱() {
        // given
        String startLine = "GET /index.html HTTP/1.1";

        // when
        StartLine result = StartLine.from(startLine);

        // then
        assertEquals("GET", result.method());
        assertEquals("/index.html", result.path());
        assertEquals("HTTP/1.1", result.version());
    }

    @Test
    void 쿼리_파라미터가_있는_경로_파싱() {
        // given
        String startLine = "GET /login?account=admin&password=123 HTTP/1.1";

        // when
        StartLine result = StartLine.from(startLine);

        // then
        assertEquals("GET", result.method());
        assertEquals("/login", result.path());
        assertEquals("HTTP/1.1", result.version());
    }

    @Test
    void POST_요청_파싱() {
        // given
        String startLine = "POST /api/users HTTP/1.1";

        // when
        StartLine result = StartLine.from(startLine);

        // then
        assertEquals("POST", result.method());
        assertEquals("/api/users", result.path());
        assertEquals("HTTP/1.1", result.version());
    }

    @Test
    void null_입력시_예외_발생() {
        // given
        String startLine = null;

        // when & then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> StartLine.from(startLine)
        );
        assertEquals("Start line is null or empty", exception.getMessage());
    }

    @Test
    void 빈_문자열_입력시_예외_발생() {
        // given
        String startLine = "";

        // when & then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> StartLine.from(startLine)
        );
        assertEquals("Start line is null or empty", exception.getMessage());
    }

    @Test
    void 공백만_있는_문자열_입력시_예외_발생() {
        // given
        String startLine = "   ";

        // when & then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> StartLine.from(startLine)
        );
        assertEquals("Start line is null or empty", exception.getMessage());
    }

    @Test
    void 잘못된_형식의_스타트라인_예외_발생() {
        // given
        String startLine = "GET /index.html";

        // when & then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> StartLine.from(startLine)
        );
        assertEquals("Invalid start line: GET /index.html", exception.getMessage());
    }

    @Test
    void 너무_많은_토큰이_있는_경우_예외_발생() {
        // given
        String startLine = "GET /index.html HTTP/1.1 extra";

        // when & then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> StartLine.from(startLine)
        );
        assertEquals("Invalid start line: GET /index.html HTTP/1.1 extra", exception.getMessage());
    }
}
