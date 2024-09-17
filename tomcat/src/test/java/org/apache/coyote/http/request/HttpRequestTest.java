package org.apache.coyote.http.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import org.apache.catalina.session.Session;
import org.apache.coyote.http.HttpMethod;
import org.junit.jupiter.api.Test;

public class HttpRequestTest {

    @Test
    void HttpRequest를_생성할_수_있다() throws IOException {
        // given
        String request = "GET /index.html HTTP/1.1\r\n"
                + "Host: localhost:8080\r\n"
                + "Content-Length: 13\r\n"
                + "Cookie: JSESSIONID=abc123\r\n"
                + "\r\n"
                + "username=John";

        BufferedReader bufferedReader = new BufferedReader(new StringReader(request));

        // when
        HttpRequest httpRequest = new HttpRequest(bufferedReader);

        // then
        assertThat(httpRequest.getHttpMethod()).isEqualTo(HttpMethod.GET);
        assertThat(httpRequest.getRequestURI()).isEqualTo("/index.html");
        assertThat(httpRequest.getBody().get("username")).isEqualTo("John");
    }

    @Test
    void 요청에_세션을_생성할_수_있다() throws IOException {
        // given
        String request = "GET /index.html HTTP/1.1\r\n"
                + "Host: localhost:8080\r\n"
                + "Content-Length: 13\r\n"
                + "\r\n"
                + "username=John";

        BufferedReader bufferedReader = new BufferedReader(new StringReader(request));

        // when
        HttpRequest httpRequest = new HttpRequest(bufferedReader);
        Session session = httpRequest.createSession();

        // then
        assertThat(session).isNotNull();
        assertThat(httpRequest.findSession()).isEmpty();
    }

    @Test
    void 요청의_헤더를_파싱할_수_있다() throws IOException {
        // given
        String request = "POST /login HTTP/1.1\r\n"
                + "Host: localhost:8080\r\n"
                + "Content-Length: 13\r\n"
                + "Cookie: JSESSIONID=xyz456\r\n"
                + "\r\n"
                + "username=Jane";

        BufferedReader bufferedReader = new BufferedReader(new StringReader(request));

        // when
        HttpRequest httpRequest = new HttpRequest(bufferedReader);

        // then
        assertThat(httpRequest.getHttpMethod()).isEqualTo(HttpMethod.POST);
        assertThat(httpRequest.getRequestURI()).isEqualTo("/login");
        assertThat(httpRequest.getBody().get("username")).isEqualTo("Jane");
    }

    @Test
    void 잘못된_요청의_시작줄이_있으면_예외를_발생시킨다() {
        // given
        String request = "\r\n";
        BufferedReader bufferedReader = new BufferedReader(new StringReader(request));

        // when & then
        assertThatThrownBy(() -> new HttpRequest(bufferedReader))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("요청의 시작줄이 비어있습니다.");
    }
}
