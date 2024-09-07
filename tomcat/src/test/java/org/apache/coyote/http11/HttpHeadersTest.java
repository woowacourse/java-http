package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class HttpHeadersTest {

    @Nested
    class HTTP_헤더 {

        @Test
        void 문자열로_헤더를_생성할_수_있다() {
            // given
            String source = "Content-Type: text/js;charset=utf-8 ";
            HttpHeaders httpHeaders = new HttpHeaders();

            // when
            httpHeaders.addByString(source);

            // then
            assertThat(httpHeaders.findByKey("Content-Type")).isEqualTo("text/js;charset=utf-8");
        }

        @Test
        void 특수문자포함_문자열로_헤더를_생성할_수_있다() {
            // given
            String source = "Accept: text/html,*/*;q=0.1 ";
            HttpHeaders httpHeaders = new HttpHeaders();

            // when
            httpHeaders.addByString(source);

            // then
            assertThat(httpHeaders.findByKey("Accept")).isEqualTo("text/html,*/*;q=0.1");
        }
    }
}
