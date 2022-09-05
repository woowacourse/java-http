package org.apache.coyote.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    @DisplayName("쿼리를 제외하고 자원의 확장자를 포함한 URI 를 가져올 수 있다.")
    @Test
    void getRequestUrlWithoutQuery() {
        // given
        String startLine = "GET /login?account=dwoo&password=123 HTTP1.1 ";
        final HttpRequest sut = new HttpRequest(startLine, Map.of(), "");

        // when
        final String url = sut.getRequestUrlWithoutQuery();

        // then
        assertThat(url).isEqualTo("/login.html");
    }

    @DisplayName("확장자를 추가하고, 쿼리스트링을 포함한 URI 를 가져올 수 있다.")
    @Test
    void getRequestUrl() {
        // given
        String startLine = "GET /login?account=dwoo&password=123 HTTP1.1 ";
        final HttpRequest sut = new HttpRequest(startLine, Map.of(), "");

        // when
        final String url = sut.getRequestUrl();

        // then
        assertThat(url).isEqualTo("/login.html?account=dwoo&password=123");
    }
}
