package org.apache.coyote.common;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class RequestUriTest {

    @Test
    void httpMethod가_null이면_예외() {
        // given
        HttpMethod method = null;
        HttpPath path = HttpPath.from("/");
        HttpProtocol protocol = HttpProtocol.HTTP11;

        // when & then
        assertThatThrownBy(() -> new RequestUri(method, path, protocol))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("HttpMethod는 null이 될 수 없습니다.");
    }

    @Test
    void path가_null이면_예외() {
        // given
        HttpMethod method = HttpMethod.GET;
        HttpPath path = null;
        HttpProtocol protocol = HttpProtocol.HTTP11;

        // when & then
        assertThatThrownBy(() -> new RequestUri(method, path, protocol))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Path는 null이 될 수 없습니다.");
    }

    @Test
    void httpProtocol이_null이면_예외() {
        // given
        HttpMethod method = HttpMethod.GET;
        HttpPath path = HttpPath.from("/");
        HttpProtocol protocol = null;

        // when & then
        assertThatThrownBy(() -> new RequestUri(method, path, protocol))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("HttpProtocol은 null이 될 수 없습니다.");
    }

    @Test
    void 생성() {
        // given
        RequestUri requestUri = new RequestUri(HttpMethod.GET, HttpPath.from("/index.html"), HttpProtocol.HTTP11);

        // when & then
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(requestUri.getHttpMethod()).isEqualTo(HttpMethod.GET);
            softly.assertThat(requestUri.getHttpPath().getPath()).isEqualTo("/index.html");
            softly.assertThat(requestUri.getHttpProtocol()).isEqualTo(HttpProtocol.HTTP11);
        });
    }

    @Nested
    class from {

        @Test
        void 파라미터가_null이면_예외() {
            // when & then
            assertThatThrownBy(() -> RequestUri.from(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("requestUri는 null이 될 수 없습니다.");
        }

        @Test
        void 파라미터가_RequestUri의_형식이_아니면_예외() {
            assertThatThrownBy(() -> RequestUri.from("hello world"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("지원되지 않는 RequestURI 입니다.");
        }

        @Test
        void 생성() {
            // given
            RequestUri requestUri = RequestUri.from("GET /index.html HTTP/1.1");

            // when & then
            SoftAssertions.assertSoftly(softly -> {
                softly.assertThat(requestUri.getHttpMethod()).isEqualTo(HttpMethod.GET);
                softly.assertThat(requestUri.getHttpPath().getPath()).isEqualTo("/index.html");
                softly.assertThat(requestUri.getHttpProtocol()).isEqualTo(HttpProtocol.HTTP11);
            });
        }
    }
}
