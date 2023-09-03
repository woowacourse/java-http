package org.apache.coyote.http11.request.requestLine;

import org.apache.coyote.http11.common.HttpVersion;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HttpVersionTest {

    @Test
    @DisplayName("문자열로 주어진 HTTP 버전 정보를 HttpVersion 타입으로 변환한다.")
    void stringToHttpVersion() {
        // given
        final String httpVersion = "HTTP/1.1";

        // when
        final HttpVersion actual = HttpVersion.from(httpVersion);

        // then
        assertThat(actual).isEqualTo(HttpVersion.HTTP_1_1);
    }
}
