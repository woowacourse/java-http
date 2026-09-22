package org.apache.coyote.http11.response;

import org.apache.coyote.http11.HttpVersion;
import org.apache.coyote.http11.response.line.HttpStatus;
import org.apache.coyote.http11.response.line.StatusLine;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StatusLineTest {

    @Test
    @DisplayName("status line은 버전, 상태 코드, 상태 메시지로 조립된다.")
    void serialize() {
        // given
        StatusLine statusLine = StatusLine.from(HttpStatus.OK);

        // when
        String actual = statusLine.serialize();

        // then
        assertThat(actual).isEqualTo("HTTP/1.1 200 OK");
    }

    @Test
    @DisplayName("from으로 생성하면 HTTP/1.1 버전을 사용한다.")
    void defaultHttpVersion() {
        // given & when
        String actual = StatusLine.from(HttpStatus.NOT_FOUND).serialize();

        // then
        assertThat(actual).isEqualTo("HTTP/1.1 404 Not Found");
    }

    @Test
    @DisplayName("버전을 직접 지정할 수 있다.")
    void customHttpVersion() {
        // given
        StatusLine statusLine = new StatusLine(HttpVersion.HTTP_1_0, HttpStatus.FOUND);

        // when
        String actual = statusLine.serialize();

        // then
        assertThat(actual).isEqualTo("HTTP/1.0 302 FOUND");
    }

}
