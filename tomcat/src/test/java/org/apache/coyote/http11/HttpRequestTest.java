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
        assertThat(request.getParameter("account")).isEqualTo("gugu");
        assertThat(request.getParameter("password")).isEqualTo("password");
    }

    @Test
    void query_string이_없으면_parameter는_null이다() {
        HttpRequest request = HttpRequest.from("GET /login HTTP/1.1");

        assertThat(request.getParameter("account")).isNull();
    }

    @Test
    void query_string의_인코딩된_문자를_디코딩한다() {
        HttpRequest request = HttpRequest.from(
                "GET /login?account=gugu%40email.com&password=pass%20word HTTP/1.1"
        );

        assertThat(request.getParameter("account")).isEqualTo("gugu@email.com");
        assertThat(request.getParameter("password")).isEqualTo("pass word");
    }
}
