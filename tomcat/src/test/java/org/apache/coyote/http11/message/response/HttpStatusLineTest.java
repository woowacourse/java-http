package org.apache.coyote.http11.message.response;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpStatusLineTest {

    private HttpStatusLine httpStatusLine;

    @BeforeEach
    void setUp() {
        httpStatusLine = new HttpStatusLine(HttpStatus.OK);
    }

    @DisplayName("HttpStatusLine 객체는 기본 HTTP 버전과 상태 코드로 초기화된다.")
    @Test
    void constructorWithDefaultVersion() {
        // given
        HttpStatusLine statusLine = new HttpStatusLine(HttpStatus.CREATED);

        // when
        String result = statusLine.toString();

        // then
        assertThat(result).isEqualTo("HTTP/1.1 201 Created ");
    }

    @DisplayName("HttpStatusLine 객체는 지정된 HTTP 버전과 상태 코드로 초기화된다.")
    @Test
    void constructorWithCustomVersion() {
        // given
        HttpStatusLine statusLine = new HttpStatusLine("HTTP/2.0", HttpStatus.NOT_FOUND);

        // when
        String result = statusLine.toString();

        // then
        assertThat(result).isEqualTo("HTTP/2.0 404 Not Found ");
    }

    @DisplayName("setHttpStatus() 메서드는 상태 코드를 업데이트 한다.")
    @Test
    void setHttpStatus() {
        // given
        httpStatusLine.setHttpStatus(HttpStatus.BAD_REQUEST);

        // when
        String result = httpStatusLine.toString();

        // then
        assertThat(result).isEqualTo("HTTP/1.1 400 Bad Request ");
    }

    @DisplayName("toString() 메서드는 HTTP 버전과 상태 코드, 상태 메시지를 포함한다.")
    @Test
    void toStringTest() {
        // when
        String result = httpStatusLine.toString();

        // then
        assertThat(result).isEqualTo("HTTP/1.1 200 OK ");
    }
}
