package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import support.StubSocket;

class Http11ProcessorTest {

    @Test
    void process() {
        // given
        final var socket = new StubSocket();
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        List<String> expected = List.of(
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 12 ",
                "",
                "Hello world!");
        List<String> actual = List.of(socket.output().split("\r\n"));

        assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    void index() throws IOException {
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
        final List<String> expected = List.of(
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 5564 ",
                "",
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()))
        );
        final List<String> actual = List.of(socket.output().split("\r\n"));

        assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    @DisplayName("GET /login 요청시 200을 반환한다.")
    void get_login_return_200() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Accept: */*",
                "",
                "");
        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/login.html");
        final String content = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        final List<String> expected = List.of(
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + content.getBytes().length + " ",
                "",
                content
        );
        final List<String> actual = List.of(socket.output().split("\r\n"));

        assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    @DisplayName("POST /login 요청으로 로그인에 성공할 시 302와 Location: /index.html과 쿠키를 반환한다.")
    void post_login_return_302_and_index_page_and_cookie() {
        // given
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Host: localhost:8080",
                "Content-Length: 30",
                "Content-Type: application/x-www-form-urlencoded",
                "Accept: */*",
                "",
                "account=gugu&password=password");
        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        List<String> expected = List.of(
                "HTTP/1.1 302 Found ",
                "Location: /index.html ");
        List<String> actual = List.of(socket.output().split("\r\n"));

        assertThat(actual)
                .containsAnyElementsOf(expected)
                .anyMatch(line -> line.startsWith("Set-Cookie: JSESSIONID="));
    }

    @Test
    @DisplayName("POST /login 요청으로 로그인에 실패할 시 302와 Location: /402.html을 반환한다.")
    void post_login_return_302_and_401_page() {
        // given
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Host: localhost:8080",
                "Content-Length: 32",
                "Content-Type: application/x-www-form-urlencoded",
                "Accept: */*",
                "",
                "account=gugu&password=password12");
        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        List<String> expected = List.of(
                "HTTP/1.1 302 Found ",
                "Location: /401.html ");
        List<String> actual = List.of(socket.output().split("\r\n"));

        assertThat(actual)
                .containsAnyElementsOf(expected)
                .noneMatch(line -> line.startsWith("Set-Cookie: JSESSIONID="));
    }

    @Test
    @DisplayName("POST /register 요청으로 회원가입 성공 시 302를 반환한다.")
    void post_register_return_302() {
        // given
        final String httpRequest = String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 80 ",
                "Content-Type: application/x-www-form-urlencoded ",
                "Accept: */* ",
                "",
                "account=account&password=password&email=email");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final List<String> expected = List.of(
                "HTTP/1.1 302 Found ",
                "Location: /index.html "
        );
        final List<String> actual = List.of(socket.output().split("\r\n"));
        assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }
}
