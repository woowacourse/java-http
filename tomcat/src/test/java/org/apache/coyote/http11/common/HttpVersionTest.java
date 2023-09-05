package org.apache.coyote.http11.common;

import static org.apache.coyote.http11.common.HttpVersion.HTTP_1_1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.apache.coyote.http11.exception.InvalidHttpVersionException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpVersionTest {

    @Test
    @DisplayName("일치하는 HttpVersion을 반환한다.")
    void findByVersion() {
        //given
        String version = "HTTP/1.1";

        //when
        final HttpVersion httpVersion = HttpVersion.findByVersion(version);

        //then
        assertThat(httpVersion).isEqualTo(HTTP_1_1);
    }

    @Test
    @DisplayName("일치하는 version이 없는 경우 예외가 발상한다.")
    void findByVersion_fail() {
        //given
        final String version = "INVALID";

        //when & then
        assertThatThrownBy(() -> HttpVersion.findByVersion(version))
                .isInstanceOf(InvalidHttpVersionException.class)
                .hasMessage("유효하지 않은 HTTP version 입니다.");

        //then
    }
}
