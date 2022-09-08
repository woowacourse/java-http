package nextstep.org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.apache.coyote.http11.Http11Processor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import support.StubSocket;

class Http11ProcessorTest {

    @DisplayName("/ 에 접속시 Hello world! 문구를 출력한다.")
    @Test
    void rootPathHelloWorld() {
        // given
        StubSocket socket = new StubSocket();
        Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        String expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 12 ",
                "",
                "Hello world!");

        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("정적 파일을 서빙한다.")
    @CsvSource(value = {
            "/index.html,text/html",
            "/login.html,text/html",
            "/register.html,text/html",
            "/401.html,text/html",
            "/404.html,text/html",
            "/500.html,text/html",
            "/css/styles.css,text/css",
            "/js/scripts.js,text/javascript",
            "/assets/chart-area.js,text/javascript",
            "/assets/chart-bar.js,text/javascript",
            "/assets/chart-pie.js,text/javascript",
            "/assets/img/error-404-monochrome.svg,image/svg+xml",
    })
    @ParameterizedTest
    void serveStaticFiles(final String filePath, final String contentType) throws IOException {
        // given
        String httpRequest = String.join("\r\n",
                "GET " + filePath + " HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        StubSocket socket = new StubSocket(httpRequest);
        Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        URL resource = getClass().getClassLoader().getResource("static" + filePath);
        String fileContent = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        int contentLength = fileContent.getBytes().length;

        String expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: " + contentType + ";charset=utf-8 \r\n" +
                "Content-Length: " + contentLength + " \r\n" +
                "\r\n" + fileContent;

        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("/login에 접속하면, login.html를 서빙한다.")
    @Test
    void serveLoginHtmlOnLoginPath() throws IOException {
        // given
        String httpRequest = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        StubSocket socket = new StubSocket(httpRequest);
        Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        URL resource = getClass().getClassLoader().getResource("static/login.html");
        String fileContent = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        int contentLength = fileContent.getBytes().length;

        String expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: " + contentLength + " \r\n" +
                "\r\n" + fileContent;

        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("로그인이 성공하면 응답의 상태코드는 302이고, Location 헤더는 /index.html이다.")
    @Test
    void loginSuccess() {
        // given
        String body = "account=gugu&password=password";
        String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: " + body.getBytes().length,
                "",
                body);

        StubSocket socket = new StubSocket(httpRequest);
        Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);
        String actual = socket.output();

        // then
        assertAll(() -> {
            assertThat(actual).contains("HTTP/1.1 302 FOUND");
            assertThat(actual).contains("Location: /index.html");
        });
    }

    @DisplayName("로그인이 실패하면 응답의 상태코드는 302이고, Location 헤더는 /401.html이다.")
    @Test
    void loginFail() {
        // given
        String body = "account=gugu&password=wrong-password";
        String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: " + body.getBytes().length,
                "",
                body);

        StubSocket socket = new StubSocket(httpRequest);
        Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        String expected = "HTTP/1.1 302 FOUND \r\n" +
                "Location: /401.html \r\n" +
                "\r\n";
        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("/register에 접속하면, register.html를 서빙한다.")
    @Test
    void serveRegisterHtmlOnRegisterPath() throws IOException {
        // given
        String httpRequest = String.join("\r\n",
                "GET /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        StubSocket socket = new StubSocket(httpRequest);
        Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        URL resource = getClass().getClassLoader().getResource("static/register.html");
        String fileContent = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        int contentLength = fileContent.getBytes().length;

        String expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: " + contentLength + " \r\n" +
                "\r\n" + fileContent;

        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("회원가입에 성공하면 응답의 상태코드는 302이고, Location 헤더는 /index.html이다.")
    @Test
    void registerSuccess() {
        // given
        String body = "account=gugu&password=password&email=gugu@gmail.com";
        String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: " + body.getBytes().length,
                "",
                body);

        StubSocket socket = new StubSocket(httpRequest);
        Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);
        String actual = socket.output();

        // then
        assertAll(() -> {
            assertThat(actual).contains("HTTP/1.1 302 FOUND");
            assertThat(actual).contains("Location: /index.html");
        });
    }
}
