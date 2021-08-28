package nextstep.jwp.httpserver.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Headers 단위 테스트")
class HeadersTest {

    @Test
    @DisplayName("헤더 추가")
    void addHeader() {
        // given
        Headers headers = new Headers();
        int originalSize = headers.getHeaders().size();

        // when
        headers.addHeader("Location", "1");
        int actual = headers.getHeaders().size();

        // then
        assertThat(actual).isEqualTo(originalSize + 1);
    }

    @Test
    @DisplayName("content-length 헤더가 없는 경우 0을 리턴")
    void noContentLength() {
        // given
        Headers headers = new Headers();

        // when
        String contentLength = headers.contentLength();

        // then
        assertThat(contentLength).isEqualTo("0");
    }

    @Test
    @DisplayName("content-length 헤더 찾기")
    void contentLength() {
        // given
        Headers headers = new Headers();
        headers.addHeader("Content-Length", "10");

        // when
        String contentLength = headers.contentLength();

        // then
        assertThat(contentLength).isEqualTo("10");
    }
}
