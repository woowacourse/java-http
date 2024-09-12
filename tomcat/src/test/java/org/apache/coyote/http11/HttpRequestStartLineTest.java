package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.techcourse.exception.client.BadRequestException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class HttpRequestStartLineTest {

    @Nested
    class 시작_라인 {
        @Test
        void 문자열로_객체를_생성할_수_있다() {
            // given & when
            HttpRequestStartLine startLine = HttpRequestStartLine.create(
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

        @Test
        void 메서드가_없는_시작_라인_요청시_에러가_발생한다() {
            // given
            String startLine = "/login?account=gugu&password=password HTTP/1.1 ";

            // when & then
            assertThatThrownBy(() -> HttpRequestStartLine.create(startLine))
                    .isExactlyInstanceOf(BadRequestException.class);
        }

        @Test
        void URI가_없는_시작_라인_요청시_에러가_발생한다() {
            // given
            String startLine = "GET HTTP/1.1 ";

            // when & then
            assertThatThrownBy(() -> HttpRequestStartLine.create(startLine))
                    .isExactlyInstanceOf(BadRequestException.class);
        }

        @Test
        void HTTP버전이_없는_시작_라인_요청시_에러가_발생한다() {
            // given
            String startLine = "GET /login?account=gugu&password=password ";

            // when & then
            assertThatThrownBy(() -> HttpRequestStartLine.create(startLine))
                    .isExactlyInstanceOf(BadRequestException.class);
        }
    }
}
