package org.apache.coyote.http11;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HttpRequestTest {

    @Test
    void requestLine에서_method와_path를_분리한다() {
        HttpRequest request = HttpRequest.from("GET /index.html HTTP/1.1");

        assertThat(request.method()).isEqualTo("GET");
        assertThat(request.path()).isEqualTo("/index.html");
    }

    @Test
    void URI의_query_string을_파라미터로_분리한다() {
        HttpRequest request = HttpRequest.from(
                "GET /login?account=gugu&password=password HTTP/1.1"
        );

        assertThat(request.path()).isEqualTo("/login");
        assertThat(request.queryParameters().get("account")).contains("gugu");
        assertThat(request.queryParameters().get("password")).contains("password");
    }

    @Test
    void query_string이_없으면_빈_파라미터를_반환한다() {
        HttpRequest request = HttpRequest.from("GET /login HTTP/1.1");

        assertThat(request.queryParameters().get("account")).isEmpty();
    }

    @Test
    void query_string의_인코딩된_문자를_디코딩한다() {
        HttpRequest request = HttpRequest.from(
                "GET /login?account=gugu%40email.com&password=pass%20word HTTP/1.1"
        );

        assertThat(request.queryParameters().get("account")).contains("gugu@email.com");
        assertThat(request.queryParameters().get("password")).contains("pass word");
    }
}
