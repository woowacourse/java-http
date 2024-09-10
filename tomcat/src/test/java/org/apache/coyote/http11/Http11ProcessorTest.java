package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.BaseHttpTest;
import support.StubSocket;

class Http11ProcessorTest extends BaseHttpTest {

    @Test
    @DisplayName("GET / 요청 시 index.html 파일을 반환한다")
    void process() throws URISyntaxException, IOException {
        //given
        final String httpRequest = resolveGetRequestByPath("/");
        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        final String expected = resolve200Response("html", resource);
        assertThat(socket.output()).isEqualTo(expected);

        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("GET /index.html 요청 시 index.html 파일을 반환한다")
    @Test
    void index() throws IOException, URISyntaxException {
        // given
        final String httpRequest = resolveGetRequestByPath("/index.html");
        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        final String expected = resolve200Response("html", resource);
        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("GET /css/styles.css 요청 시 style.css 파일을 반환한다")
    @Test
    void style() throws IOException, URISyntaxException {
        // given
        final String httpRequest = resolveGetRequestByPath("/css/styles.css");
        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/css/styles.css");
        final String expected = resolve200Response("css", resource);
        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("로그인 성공 시: 302를 반환하고 /index.html로 리다이렉트한다.")
    @Test
    void loginSuccess() {
        // given
        final String httpRequest = resolveGetRequestByPath("/login?account=gugu&password=password");
        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final String expected = resolve302Response("/index.html");

        assertThat(socket.output()).contains(expected);
    }

    @DisplayName("로그인 실패 시: /401.html을 반환한다")
    @Test
    void loginFail() throws URISyntaxException, IOException {
        // given
        final String httpRequest = resolveGetRequestByPath("/login?account=wrong&password=wrongpassword");
        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/401.html");
        final String expected = resolve200Response("html", resource);

        assertThat(socket.output()).contains(expected);
    }

    @DisplayName("GET /register 요청 시 register.html 파일을 반환한다")
    @Test
    void register() throws IOException, URISyntaxException {
        // given
        final String httpRequest = resolveGetRequestByPath("/register");
        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/register.html");
        final String expected = resolve200Response("html", resource);
        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("POST /register 요청으로 새로운 회원을 가입할 시, 가입 완료 후 index.html로 리다이렉트한다")
    @Test
    void registerNewUser() throws IOException, URISyntaxException {
        // given
        final String httpRequest = "POST /register HTTP/1.1\n"
                + "Host: localhost:8080\n"
                + "Connection: keep-alive\n"
                + "Content-Length: 80\n"
                + "Content-Type: application/x-www-form-urlencoded\n"
                + "Accept: */*\n"
                + "\n"
                + "account=gugu&password=password&email=hkkang%40woowahan.com\n";

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final String expected = resolve302Response("/index.html");
        assertThat(socket.output()).startsWith(expected);
    }
}
