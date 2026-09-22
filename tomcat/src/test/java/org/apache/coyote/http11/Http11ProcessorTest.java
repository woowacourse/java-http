package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.junit.jupiter.api.Test;
import support.StubSocket;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class Http11ProcessorTest {

    @Test
    void GET_login은_login_html을_응답한다() throws IOException {
        // given
        String httpRequest = String.join("\r\n",
                "GET /login HTTP/1.1",
                "Host: localhost:8080",
                "",
                "");

        // when
        String response = process(httpRequest);

        // then
        URL resource = getClass().getClassLoader().getResource("static/login.html");
        String responseBody = Files.readString(new File(resource.getFile()).toPath());
        assertThat(response)
                .startsWith("HTTP/1.1 200 OK \r\n")
                .endsWith(responseBody);
    }

    @Test
    void POST_login에_성공하면_index로_redirect한다() {
        // given
        String body = "account=usher&password=password";
        String httpRequest = formRequest("/login", body);

        // when
        String response = process(httpRequest);

        // then
        assertThat(response)
                .startsWith("HTTP/1.1 302 Found \r\n")
                .contains("Location: /index.html \r\n");
    }

    @Test
    void POST_login에서_계정을_찾지_못하면_401_html로_redirect한다() {
        // given
        String body = "account=unknown&password=password";
        String httpRequest = formRequest("/login", body);

        // when
        String response = process(httpRequest);

        // then
        assertThat(response)
                .startsWith("HTTP/1.1 302 Found \r\n")
                .contains("Location: /401.html \r\n");
    }

    @Test
    void POST_login에서_비밀번호가_다르면_401_html로_redirect한다() {
        // given
        String body = "account=usher&password=wrong";
        String httpRequest = formRequest("/login", body);

        // when
        String response = process(httpRequest);

        // then
        assertThat(response)
                .startsWith("HTTP/1.1 302 Found \r\n")
                .contains("Location: /401.html \r\n");
    }

    @Test
    void GET_register는_register_html을_응답한다() throws IOException {
        // given
        String httpRequest = String.join("\r\n",
                "GET /register HTTP/1.1",
                "Host: localhost:8080",
                "",
                "");
        StubSocket socket = new StubSocket(httpRequest);
        Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        URL resource = getClass().getClassLoader().getResource("static/register.html");
        String responseBody = Files.readString(new File(resource.getFile()).toPath());
        assertThat(socket.output())
                .startsWith("HTTP/1.1 200 OK \r\n")
                .endsWith(responseBody);
    }

    @Test
    void POST_register는_사용자를_저장하고_index로_redirect한다() {
        // given
        String account = "usher-" + UUID.randomUUID();
        String body = "account=" + account + "&password=password&email=usher%40woowahan.com";
        String httpRequest = String.join("\r\n",
                "POST /register HTTP/1.1",
                "Content-Type: application/x-www-form-urlencoded",
                "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length,
                "",
                body);
        StubSocket socket = new StubSocket(httpRequest);
        Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        User savedUser = InMemoryUserRepository.findByAccount(account).orElseThrow();
        assertThat(savedUser.checkPassword("password")).isTrue();
        assertThat(socket.output())
                .startsWith("HTTP/1.1 302 Found \r\n")
                .contains("Location: /index.html \r\n")
                .endsWith("\r\n\r\n");
    }

    @Test
    void process() {
        // given
        final var socket = new StubSocket();
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        var expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 12 ",
                "",
                "Hello world!");

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void 요청_첫_줄이_없어도_예외가_발생하지_않는다() {
        // given
        final var socket = new StubSocket("");
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).contains("Hello world!");
    }

    @Test
    void index() throws IOException {
        // given
        final String httpRequest= String.join("\r\n",
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
        var expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: 5564 \r\n" +
                "\r\n"+
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void css() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /css/styles.css HTTP/1.1 ",
                "Host: localhost:8080 ",
                "",
                "");
        final var socket = new StubSocket(httpRequest);
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/css/styles.css");
        final String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        final String expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/css \r\n" +
                "Content-Length: " + responseBody.getBytes().length + " \r\n" +
                "\r\n" +
                responseBody;

        assertThat(socket.output()).isEqualTo(expected);
    }

    private String formRequest(String path, String body) {
        return String.join("\r\n",
                "POST " + path + " HTTP/1.1",
                "Content-Type: application/x-www-form-urlencoded",
                "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length,
                "",
                body);
    }

    private String process(String httpRequest) {
        StubSocket socket = new StubSocket(httpRequest);
        Http11Processor processor = new Http11Processor(socket);
        processor.process(socket);
        return socket.output();
    }
}
