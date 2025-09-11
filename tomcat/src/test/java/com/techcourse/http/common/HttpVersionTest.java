package com.techcourse.http.common;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.techcourse.exception.NotFoundException;
import com.techcourse.http.HttpVersion;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpVersionTest {

    @DisplayName("http version 찾기")
    @Test
    void fromTest1() {
        // given
        String protocolString = "HTTP/1.1";

        // when
        HttpVersion httpVersion = HttpVersion.from(protocolString);

        // then
        assertThat(httpVersion).isEqualTo(HttpVersion.HTTP_1_1);
        assertThat(httpVersion.toProtocolString()).isEqualTo("HTTP/1.1");
    }

    @DisplayName("존재하지 않는 버전인 경우")
    @Test
    void fromTest2() {
        // given
        String protocolString = "HTTP/4.0";

        // when & then
        assertThatThrownBy(() -> HttpVersion.from(protocolString))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 http version 입니다.");
    }
}
