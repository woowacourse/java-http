package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.StubSocket;

class Http11ProcessorTest {

    @DisplayName("uri가 / 일경우 index.html을 반환한다.")
    @Test
    void response_index_html_When_request_default() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        String expectedContent = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).contains(expectedContent);
    }

    @DisplayName("/login Get요청시 login페이지를 반환한다.")
    @Test
    void response_login_html_When_request_get_login() throws IOException {
        // given
        String httpRequest = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");
        StubSocket socket = new StubSocket(httpRequest);
        Http11Processor processor = new Http11Processor(socket);
        URL resource = getClass().getClassLoader().getResource("static/login.html");
        String expectedBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).contains(expectedBody);
    }

    @DisplayName("/login Post요청시, 성공 -> default페이지 리다이렉션")
    @Test
    void response_redirect_default_uri_When_request_post_login_success() throws IOException {
        // given
        String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Type: application/x-www-form-urlencoded",
                "Content-Length: " + "account=gugu&password=password".length(),
                "",
                "account=gugu&password=password");
        StubSocket socket = new StubSocket(httpRequest);
        Http11Processor processor = new Http11Processor(socket);

        String defaultLocation = "http://localhost:8080";

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).contains(defaultLocation);
    }

    @DisplayName("/login Post요청시, 실패 -> 401페이지 반환")
    @Test
    void response_401_html_When_request_post_login_fail() throws IOException {
        // given
        String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Type: application/x-www-form-urlencoded",
                "Content-Length: " + "account=gugu&password=password".length(),
                "",
                "account=gugu&password=failPassword");
        StubSocket socket = new StubSocket(httpRequest);
        Http11Processor processor = new Http11Processor(socket);

        URL resource = getClass().getClassLoader().getResource("static/401.html");
        String expectedBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).contains(expectedBody);
    }


    @DisplayName("/register Post요청시, 성공-> index페이지 반환")
    @Test
    void response_redirect_default_uri_When_request_post_register() {
        // given
        String httpRequest = String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Type: application/x-www-form-urlencoded",
                "Content-Length: " + "account=gugu&password=password".length(),
                "",
                "account=dodo&email=dodo@a.com&password=password");
        StubSocket socket = new StubSocket(httpRequest);
        Http11Processor processor = new Http11Processor(socket);

        String defaultLocation = "http://localhost:8080";

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).contains(defaultLocation);
    }

    @DisplayName("/register Get요청시, 성공-> index페이지 반환")
    @Test
    void response_redirect_default_uri_When_request_get_register() throws IOException {
        // given
        String httpRequest = String.join("\r\n",
                "GET /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");
        StubSocket socket = new StubSocket(httpRequest);
        Http11Processor processor = new Http11Processor(socket);
        URL resource = getClass().getClassLoader().getResource("static/register.html");
        String expectedBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).contains(expectedBody);
    }
}
