package nextstep.org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import nextstep.jwp.config.JwpRequestMapping;
import org.apache.coyote.http11.Http11Processor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.StubSocket;

class Http11ProcessorTest {

    @DisplayName("`/`로 요청시 웰컴 페이지를 반환한다.")
    @Test
    void process() {
        // given
        final var socket = new StubSocket();
        final var processor = new Http11Processor(socket, new JwpRequestMapping());

        // when
        processor.process(socket);

        // then
        final var expected = String.join("\r\n",
                "HTTP/1.1 200 OK",
                "Content-Length: 12",
                "Content-Type: text/html;charset=utf-8",
                "",
                "Hello world!");

        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("`/index.html`로 요청시 정적 리소스 파일인 index.html을 응답한다.")
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
        final Http11Processor processor = new Http11Processor(socket, new JwpRequestMapping());

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        final var expected = String.join("\r\n",
                "HTTP/1.1 200 OK",
                "Content-Length: 5564",
                "Content-Type: text/html;charset=utf-8",
                "",
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()))
        );

        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("css 경로로 요청시 요청한 css 파일의 본문을 응답한다.")
    @Test
    void css() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /css/styles.css HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket, new JwpRequestMapping());

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/css/styles.css");
        final byte[] bytes = Files.readAllBytes(new File(resource.getFile()).toPath());

        final var expected = String.join("\r\n",
                "HTTP/1.1 200 OK",
                "Content-Length: " + bytes.length,
                "Content-Type: text/css;charset=utf-8",
                "",
                new String(bytes)
        );

        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("javascript 경로로 요청시 요청한 javascript 파일의 본문을 응답한다.")
    @Test
    void javascript() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /assets/chart-area.js HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket, new JwpRequestMapping());

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/assets/chart-area.js");
        final byte[] bytes = Files.readAllBytes(new File(resource.getFile()).toPath());

        final var expected = String.join("\r\n",
                "HTTP/1.1 200 OK",
                "Content-Length: " + bytes.length,
                "Content-Type: text/javascript;charset=utf-8",
                "",
                new String(bytes)
        );

        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("요청한 정적 리소스 파일이 존재하지 않는 경우 404 NotFound 페이지를 출력하고 404 StatusCode를 반환한다.")
    @Test
    void notFound() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /test.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket, new JwpRequestMapping());

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/404.html");
        final byte[] bytes = Files.readAllBytes(new File(resource.getFile()).toPath());

        final var expected = String.join("\r\n",
                "HTTP/1.1 404 Not Found",
                "Content-Length: " + bytes.length,
                "Content-Type: text/html;charset=utf-8",
                "",
                new String(bytes)
        );

        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("로그인 성공 시 302 상태코드를 반환하고 /index 로 리다이렉트 한다.")
    @Test
    void loginSuccess_redirect() {
        // given
        final String inputData = "account=gugu&password=password";

        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: " + inputData.getBytes().length,
                "",
                inputData);

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket, new JwpRequestMapping());

        // when
        processor.process(socket);

        // then
        final var expectedLocation = "Location: /index";
        final var expectedResponseLine = "HTTP/1.1 302 Found";

        Assertions.assertAll(
                () -> assertThat(socket.output()).contains(expectedLocation),
                () -> assertThat(socket.output()).contains(expectedResponseLine)
        );
    }

    @DisplayName("로그인 이후 /login 으로 요청 시 세션을 확인하여 /index 로 리다이렉트 한다.")
    @Test
    void afterLogin_loginRedirect() {
        // given
        var socket = new StubSocket(loginRequest());
        final Http11Processor processor = new Http11Processor(socket, new JwpRequestMapping());

        processor.process(socket);
        final String[] split = socket.output().split("Set-Cookie: ");
        final String jSessionId = split[1].strip();

        // when
        final String request = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Cookie: " + jSessionId,
                "",
                ""
        );
        socket = new StubSocket(request);

        processor.process(socket);
        final String output = socket.output();

        // then
        final var expectedLocation = "Location: /index";
        final var expectedResponseLine = "HTTP/1.1 302 Found";

        Assertions.assertAll(
                () -> assertThat(output).contains(expectedLocation),
                () -> assertThat(output).contains(expectedResponseLine)
        );
    }

    private static String loginRequest() {
        final String inputData = "account=gugu&password=password";

        return String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: " + inputData.getBytes().length,
                "",
                inputData);
    }
}
