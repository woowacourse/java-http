package nextstep.org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.coyote.http11.Http11Processor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import support.StubSocket;

class Http11ProcessorTest {

    @Test
    @DisplayName("/ 에 요청이 들어오면 Hello world!를 출력한다.")
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
    @DisplayName("/index.hmtl에 요청이 들어오면 index.html을 출력한다.")
    void index() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Accept: */*",
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
                "\r\n" +
                new String(Files.readAllBytes(new File(Objects.requireNonNull(resource).getFile()).toPath()));

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    @DisplayName("css 파일에 대한 요청이 들어오면, 해당 css 파일을 응답한다.")
    void allowCss() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /css/styles.css HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Accept: text/css,*/*;q=0.1 ",
                "Connection: keep-alive",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/css/styles.css");
        final byte[] content = Files.readAllBytes(new File(Objects.requireNonNull(resource).getFile()).toPath());
        var expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/css;charset=utf-8 \r\n" +
                "Content-Length: " + content.length + " \r\n" +
                "\r\n" +
                new String(content);

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    @DisplayName("GET /login 요청, login.html을 보여준다.")
    void loginRequestWithoutQueryString() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Accept: */*",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/login.html");
        final byte[] content = Files.readAllBytes(new File(Objects.requireNonNull(resource).getFile()).toPath());
        var expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: " + content.length + " \r\n" +
                "\r\n" +
                new String(content);

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    @DisplayName("POST /login 요청, 로그인을 검증하고 성공: Status 302, index.html 반환")
    void loginRequestAndSuccessLogin() {
        // given
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 30 ",
                "Content-Type: application/x-www-form-urlencoded ",
                "Accept: */*",
                "",
                "account=gugu&password=password");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final String expected = "HTTP/1.1 302 Found \r\n" +
                "Location: /index.html \r\n";

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    @DisplayName("POST /login 요청, 로그인을 검증하고 실패: Status 401, 401.html 반환")
    void loginRequestAndFailLogin() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 32 ",
                "Content-Type: application/x-www-form-urlencoded ",
                "Accept: */*",
                "",
                "account=invalid&password=invalid");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);
        
        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/401.html");
        final byte[] content = Files.readAllBytes(new File(Objects.requireNonNull(resource).getFile()).toPath());
        final String expected = "HTTP/1.1 401 Unauthorized \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: " + content.length + " \r\n" +
                "\r\n" +
                new String(content);

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    @DisplayName("POST /login 요청, 해당 바디로 넘어온 값이 이미 가입한 회원이면 로그를 남긴다.")
    void validateRegisterUser() {
        // given
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 30 ",
                "Content-Type: application/x-www-form-urlencoded ",
                "Accept: */*",
                "",
                "account=gugu&password=password");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);
        final Logger logger = (Logger) LoggerFactory.getLogger(Http11Processor.class);
        final ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.start();
        logger.addAppender(appender);

        // when
        processor.process(socket);

        // then
        final List<String> logs = appender.list
                .stream()
                .map(ILoggingEvent::getMessage)
                .filter(message -> message.contains("already register account"))
                .collect(Collectors.toList());

        assertThat(logs).isNotEmpty();
    }

    @Test
    @DisplayName("잘못된 url로 요청이 들어오는 경우 404.html이 응답된다.")
    void requestWithWrongUrl() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /invalid HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Accept: */*",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/404.html");
        final byte[] content = Files.readAllBytes(new File(Objects.requireNonNull(resource).getFile()).toPath());
        var expected = "HTTP/1.1 404 Not Found \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: " + content.length + " \r\n" +
                "\r\n" +
                new String(content);

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    @DisplayName("/register로 GET 요청하면, 회원가입 페이지 register.html를 반환한다")
    void requestRegister() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Accept: */*",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/register.html");
        final byte[] content = Files.readAllBytes(new File(Objects.requireNonNull(resource).getFile()).toPath());
        var expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: " + content.length + " \r\n" +
                "\r\n" +
                new String(content);

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    @DisplayName("POST /register 요청, 이미 있는 회원인지 검증하고 회원가입 성공: Status 302, Location: /index.html")
    void successRequestRegister() {
        // given
        final String httpRequest = String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 58 ",
                "Content-Type: application/x-www-form-urlencoded ",
                "Accept: */*",
                "",
                "account=tiki&password=password&email=tiki%40woowahan.com");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final String expected = "HTTP/1.1 302 Found \r\n" +
                "Location: /index.html \r\n";
        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    @DisplayName("POST /register 요청, 이미 있는 회원이여서 회원가입 실패: Status 400, 400.html")
    void failRequestRegisterWithAlreadyRegisterAccount() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 58 ",
                "Content-Type: application/x-www-form-urlencoded ",
                "Accept: */*",
                "",
                "account=gugu&password=password&email=hkkang%40woowahan.com");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/400.html");
        final byte[] content = Files.readAllBytes(new File(Objects.requireNonNull(resource).getFile()).toPath());
        final String expected = "HTTP/1.1 400 Bad Request \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: " + content.length + " \r\n" +
                "\r\n" +
                new String(content);
        assertThat(socket.output()).isEqualTo(expected);
    }
}
