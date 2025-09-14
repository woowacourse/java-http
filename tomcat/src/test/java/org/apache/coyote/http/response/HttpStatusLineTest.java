package org.apache.coyote.http.response;

import static org.assertj.core.api.Assertions.assertThat;

import common.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpStatusLineTest {

    @Test
    @DisplayName("HTTP/1.1 200 OK 상태라인 생성")
    void createOkStatusLine() {
        // given
        final String version = "1.1";
        final HttpStatus status = HttpStatus.OK;

        // when
        final HttpStatusLine statusLine = HttpStatusLine.from(version, status);

        // then
        assertThat(statusLine.toString()).isEqualTo("HTTP/1.1 200 OK\r\n");
    }

    @Test
    @DisplayName("HTTP/1.1 404 Not Found 상태라인 생성")
    void createNotFoundStatusLine() {
        // given
        final String version = "1.1";
        final HttpStatus status = HttpStatus.NOT_FOUND;

        // when
        final HttpStatusLine statusLine = HttpStatusLine.from(version, status);

        // then
        assertThat(statusLine.toString()).isEqualTo("HTTP/1.1 404 Not Found\r\n");
    }

    @Test
    @DisplayName("HTTP/1.1 302 Found 상태라인 생성")
    void createFoundStatusLine() {
        // given
        final String version = "1.1";
        final HttpStatus status = HttpStatus.FOUND;

        // when
        final HttpStatusLine statusLine = HttpStatusLine.from(version, status);

        // then
        assertThat(statusLine.toString()).isEqualTo("HTTP/1.1 302 Found\r\n");
    }

    @Test
    @DisplayName("HTTP/1.0 200 OK 상태라인 생성")
    void createHttp10StatusLine() {
        // given
        final String version = "1.0";
        final HttpStatus status = HttpStatus.OK;

        // when
        final HttpStatusLine statusLine = HttpStatusLine.from(version, status);

        // then
        assertThat(statusLine.toString()).isEqualTo("HTTP/1.0 200 OK\r\n");
    }

    @Test
    @DisplayName("HTTP/1.1 500 Internal Server Error 상태라인 생성")
    void createInternalServerErrorStatusLine() {
        // given
        final String version = "1.1";
        final HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        // when
        final HttpStatusLine statusLine = HttpStatusLine.from(version, status);

        // then
        assertThat(statusLine.toString()).isEqualTo("HTTP/1.1 500 Internal Server Error\r\n");
    }
}
