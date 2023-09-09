package nextstep.org.apache.coyote.http11;

import org.apache.coyote.http11.Http11Processor;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import support.StubSocket;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class Http11ProcessorTest {

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
    void index() throws IOException {
        // given
        var httpRequest = String.join("\r\n",
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
        var resource = getClass().getClassLoader().getResource("static/index.html");
        var responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        var expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 5564 ",
                "",
                responseBody);

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void login_페이지_요청_테스트() throws IOException {
        // given
        var httpRequest = String.join("\r\n",
                "GET /login.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        var resource = getClass().getClassLoader().getResource("static/login.html");
        var responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        var expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 3797 ",
                "",
                responseBody);

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void login_요청_테스트() {
        // given
        var requestBody = "account=gugu&password=password";

        var httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: " + requestBody.length(),
                "Content-Type: application/x-www-form-urlencoded",
                "",
                requestBody);

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        var expected = "HTTP/1.1 302 Found";

        assertSoftly(softly -> {
            softly.assertThat(socket.output()).contains(expected);
            softly.assertThat(socket.output()).contains("Set-Cookie: JSESSIONID=");
            softly.assertThat(socket.output()).contains("Location: ");
        });
    }

    @Test
    void 없는_회원_login_실패_테스트() throws IOException {
        // given
        var requestBody = "account=hello&password=password";

        var httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: " + requestBody.length(),
                "Content-Type: application/x-www-form-urlencoded",
                "",
                requestBody);

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        var resource = getClass().getClassLoader().getResource("static/401.html");
        var responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        var expected = String.join("\r\n",
                "HTTP/1.1 401 Unauthorized ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 2426 ",
                "",
                responseBody);

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void 틀린_비밀번호_login_실패_테스트() throws IOException {
        // given
        var requestBody = "account=gugu&password=wrong";

        var httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: " + requestBody.length(),
                "Content-Type: application/x-www-form-urlencoded",
                "",
                requestBody);

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        var resource = getClass().getClassLoader().getResource("static/401.html");
        var responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        var expected = String.join("\r\n",
                "HTTP/1.1 401 Unauthorized ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 2426 ",
                "",
                responseBody);

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void register_페이지_요청_테스트() throws IOException {
        // given
        var httpRequest = String.join("\r\n",
                "GET /register.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        var resource = getClass().getClassLoader().getResource("static/register.html");
        var responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        var expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 4319 ",
                "",
                responseBody);

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void register_요청_테스트() throws IOException {
        // given
        var requestBody = "account=Kiara&email=kiara%40gmail.com&password=password";

        var httpRequest = String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: " + requestBody.length(),
                "Content-Type: application/x-www-form-urlencoded",
                "",
                requestBody);

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        var resource = getClass().getClassLoader().getResource("static/index.html");
        var responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        var expected = String.join("\r\n",
                "HTTP/1.1 302 Found ",
                "Location: /index.html ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 5564 ",
                "",
                responseBody);

        assertThat(socket.output()).isEqualTo(expected);
    }
}
