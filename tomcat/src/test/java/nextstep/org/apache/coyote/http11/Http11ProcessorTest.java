package nextstep.org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.apache.coyote.http11.Http11Processor;
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
        final var expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 12 ",
                "",
                "Hello world!");

        assertThat(socket.output()).isEqualTo(expected);
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
        final var expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: 5564 \r\n" +
                "\r\n" +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("css 요청이 들어오면 css 타입으로 응답한다.")
    @Test
    void css() {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /css/styles.css HTTP/1.1 ",
                "Accept: text/css,*/*;q=0.1 ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).contains("text/css;");
    }

    @DisplayName("login 페이지를 조회한다.")
    @Test
    void login() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/login.html");
        final byte[] body = Files.readAllBytes(new File(resource.getFile()).toPath());
        final var expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: " + body.length + " \r\n" +
                "\r\n" +
                new String(body);

        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("로그인에 성공할 시 Cookie와 리다이렉트 Location을 응답 헤더에 추가한다.")
    @Test
    void post_Login_Success() {
        // given
        final String requestBody = "account=gugu&password=password";
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: " + requestBody.getBytes().length,
                "",
                requestBody);

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertSoftly(softly -> {
            assertThat(socket.output()).contains("HTTP/1.1 302 Found");
            assertThat(socket.output()).contains("JSESSIONID");
            assertThat(socket.output()).contains("Location: /index.html");
        });
    }

    @DisplayName("로그인에 실패하면 401 에러 페이지를 응답한다.")
    @Test
    void post_Login_Unauthorized() throws IOException {
        // given
        final String requestBody = "account=zeeto&password=zeeto";
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: " + requestBody.getBytes().length,
                "",
                requestBody);

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/401.html");
        final byte[] body = Files.readAllBytes(new File(resource.getFile()).toPath());
        final var expected = "HTTP/1.1 401 Unauthorized \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: " + body.length + " \r\n" +
                "\r\n" +
                new String(body);

        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("register 페이지를 조회한다.")
    @Test
    void register() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/register.html");
        final byte[] body = Files.readAllBytes(new File(resource.getFile()).toPath());
        final var expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: " + body.length + " \r\n" +
                "\r\n" +
                new String(body);

        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("회원가입에 성공할 시 Cookie와 리다이렉트 Location을 응답 헤더에 추가한다.")
    @Test
    void post_Register_Success() {
        // given
        final String requestBody = "account=io&password=password&email=hkkang%40woowahan.com";
        final String httpRequest = String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: " + requestBody.getBytes().length,
                "",
                requestBody);

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertSoftly(softly -> {
            assertThat(socket.output()).contains("HTTP/1.1 302 Found");
            assertThat(socket.output()).contains("JSESSIONID");
            assertThat(socket.output()).contains("Location: /index.html");
        });
    }
}
