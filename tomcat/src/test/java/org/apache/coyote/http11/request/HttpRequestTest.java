package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    @Test
    void GET_요청의_요청_라인과_헤더를_파싱한다() throws IOException {
        // given
        String rawRequest = String.join("\r\n",
            "GET /login?account=gugu&password=password HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "",
            "");

        // when
        HttpRequest request = HttpRequest.from(toReader(rawRequest));

        // then
        assertThat(request.getMethod()).isEqualTo("GET");
        assertThat(request.getPath()).isEqualTo("/login");
        assertThat(request.getVersion()).isEqualTo("HTTP/1.1");
        assertThat(request.getQueryParameter("account")).isEqualTo("gugu");
        assertThat(request.getQueryParameter("password")).isEqualTo("password");
        assertThat(request.getHeader("Host")).isEqualTo("localhost:8080");
        assertThat(request.getHeader("Connection")).isEqualTo("keep-alive");
        assertThat(request.getBody()).isEmpty();
    }

    @Test
    void POST_요청의_바디를_Content_Length_만큼_읽는다() throws IOException {
        // given
        String body = "account=gugu&password=password&email=hkkang%40woowahan.com";
        String rawRequest = String.join("\r\n",
            "POST /register HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Content-Length: " + body.getBytes().length,
            "Content-Type: application/x-www-form-urlencoded ",
            "",
            body);

        // when
        HttpRequest request = HttpRequest.from(toReader(rawRequest));

        // then
        assertThat(request.isMethod("POST")).isTrue();
        assertThat(request.getPath()).isEqualTo("/register");
        assertThat(request.getBody()).isEqualTo(body);
        assertThat(request.getFormData())
            .containsEntry("account", "gugu")
            .containsEntry("password", "password")
            .containsEntry("email", "hkkang@woowahan.com");
    }

    @Test
    void 쿠키_헤더에서_값을_꺼낸다() throws IOException {
        // given
        String rawRequest = String.join("\r\n",
            "GET /login HTTP/1.1 ",
            "Cookie: yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46",
            "",
            "");

        // when
        HttpRequest request = HttpRequest.from(toReader(rawRequest));

        // then
        assertThat(request.getCookie("JSESSIONID")).isEqualTo("656cef62-e3c4-40bc-a8df-94732920ed46");
        assertThat(request.getCookie("tasty_cookie")).isEqualTo("strawberry");
        assertThat(request.getCookie("none")).isNull();
    }

    @Test
    void 쿠키_헤더가_없으면_null을_반환한다() throws IOException {
        HttpRequest request = HttpRequest.from(toReader("GET / HTTP/1.1\r\n\r\n"));

        assertThat(request.getCookie("JSESSIONID")).isNull();
        assertThat(request.hasQueryString()).isFalse();
    }

    @Test
    void 요청_라인이_비어_있으면_예외가_발생한다() {
        assertThatThrownBy(() -> HttpRequest.from(toReader("")))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 요청_라인_형식이_잘못되면_예외가_발생한다() {
        assertThatThrownBy(() -> HttpRequest.from(toReader("GET /index.html\r\n\r\n")))
            .isInstanceOf(IllegalArgumentException.class);
    }

    private BufferedReader toReader(String rawRequest) {
        return new BufferedReader(new StringReader(rawRequest));
    }
}
