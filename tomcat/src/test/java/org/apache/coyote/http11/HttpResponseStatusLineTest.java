package org.apache.coyote.http11;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpResponseStatusLineTest {

    @DisplayName("상태가 OK 인 객체를 생성한다.")
    @Test
    void createOK() {
        // given
        // when
        final HttpResponseStatusLine ok = HttpResponseStatusLine.OK();

        // then
        assertAll(
            () -> assertEquals("HTTP/1.1", ok.getHttpVersion()),
            () -> assertEquals(HttpStatusCode.OK, ok.getHttpStatus()),
            () -> assertEquals("HTTP/1.1 200 OK", ok.toString())
        );
    }

    @DisplayName("상태가 FOUND 인 객체를 생성한다.")
    @Test
    void createFOUND() {
        // given
        // when
        final HttpResponseStatusLine found = HttpResponseStatusLine.FOUND();

        // then
        assertAll(
            () -> assertEquals("HTTP/1.1", found.getHttpVersion()),
            () -> assertEquals(HttpStatusCode.FOUND, found.getHttpStatus()),
            () -> assertEquals("HTTP/1.1 302 Found", found.toString())
        );
    }
}
