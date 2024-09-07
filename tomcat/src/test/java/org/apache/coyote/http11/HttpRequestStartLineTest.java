package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class HttpRequestStartLineTest {

    @Nested
    class 시작_라인 {
        @Test
        void 문자열로_객체를_생성할_수_있다() {
            // given & when
            HttpRequestStartLine startLine = HttpRequestStartLine.createByString(
                    "GET /login?account=gugu&password=password HTTP/1.1 ");

            // then
            Assertions.assertAll(
                    () -> assertThat(startLine.getMethod()).isEqualTo(HttpMethod.GET),
                    () -> assertThat(startLine.getUri()).isEqualTo("/login?account=gugu&password=password"),
                    () -> assertThat(startLine.getHttpVersion()).isEqualTo("HTTP/1.1"),
                    () -> assertThat(startLine.getPath()).isEqualTo("/login"),
                    () -> assertThat(startLine.findQuery("account")).isEqualTo("gugu"),
                    () -> assertThat(startLine.findQuery("password")).isEqualTo("password")
            );
        }
    }
}
