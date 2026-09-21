package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import org.apache.catalina.SessionManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import support.StubSocket;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

class Http11ProcessorTest {

    private SessionManager sessionManager;

    @BeforeEach
    void setUp() {
        sessionManager = new SessionManager();
    }

    @Test
    void process() {
        // given
        final var socket = new StubSocket();
        final var processor = new Http11Processor(socket, sessionManager);

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
    void index() throws IOException, URISyntaxException {
        // given
        final String httpRequest= String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket, sessionManager);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        var expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: 5564 \r\n" +
                "\r\n"+
                new String(Files.readAllBytes(Path.of(resource.toURI())), StandardCharsets.UTF_8);

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void css() throws IOException, URISyntaxException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /css/styles.css HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Accept: text/css,*/*;q=0.1 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket, sessionManager);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/css/styles.css");
        final byte[] body = Files.readAllBytes(Path.of(resource.toURI()));
        var expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/css;charset=utf-8 \r\n" +
                "Content-Length: " + body.length + " \r\n" +
                "\r\n" +
                new String(body, StandardCharsets.UTF_8);

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void login_페이지_요청() throws URISyntaxException, IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");
        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket, sessionManager);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/login.html");
        final byte[] body = Files.readAllBytes(Path.of(resource.toURI()));
        final String expectedHeader = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: " + body.length + " \r\n" +
                "\r\n";

        assertThat(socket.output()).startsWith(expectedHeader);
    }

    @Test
    void login_성공() throws URISyntaxException, IOException {
        // given
        final String requestBody = "account=gugu&password=password";
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Content-Length: " + requestBody.getBytes(StandardCharsets.UTF_8).length + " ",
                "Content-Type: application/x-www-form-urlencoded ",
                "",
                requestBody);
        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket, sessionManager);

        // when
        processor.process(socket);

        // then
        final String expectedHeader = String.join("\r\n",
                "HTTP/1.1 302 Found ",
                "Location: /index.html ",
                "Content-Length: 0 ");

        assertThat(socket.output()).startsWith(expectedHeader);
        assertThat(socket.output()).containsPattern("Set-Cookie: JSESSIONID=.+; Path=/");
    }

    @Test
    void 로그인한_사용자는_발급받은_세션으로_인증_상태를_유지한다() {
        // given
        final String requestBody = "account=gugu&password=password";
        final String loginRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Content-Length: " + requestBody.getBytes(StandardCharsets.UTF_8).length + " ",
                "Content-Type: application/x-www-form-urlencoded ",
                "",
                requestBody);
        final var loginSocket = new StubSocket(loginRequest);
        new Http11Processor(loginSocket, sessionManager).process(loginSocket);

        final Matcher matcher = Pattern.compile("Set-Cookie: JSESSIONID=(.+?); Path=/")
                .matcher(loginSocket.output());
        assertThat(matcher.find()).isTrue();
        final String sessionId = matcher.group(1);

        // when
        final String httpRequest = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Cookie: JSESSIONID=" + sessionId + " ",
                "",
                "");
        final var socket = new StubSocket(httpRequest);
        new Http11Processor(socket, sessionManager).process(socket);

        // then
        final String expected = String.join("\r\n",
                "HTTP/1.1 302 Found ",
                "Location: /index.html ",
                "Content-Length: 0 ",
                "",
                "");

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void 정적_리소스_요청은_세션을_만들지_않는다() {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /css/styles.css HTTP/1.1 ",
                "Host: localhost:8080 ",
                "",
                "");
        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket, sessionManager);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).doesNotContain("Set-Cookie");
    }

    @Test
    void 알_수_없는_세션_아이디로_요청하면_로그인_페이지를_반환한다() throws IOException, URISyntaxException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Cookie: JSESSIONID=unknown-session-id ",
                "",
                "");
        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket, sessionManager);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/login.html");
        final byte[] body = Files.readAllBytes(Path.of(resource.toURI()));
        final String expectedHeader = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: " + body.length + " \r\n" +
                "\r\n";

        assertThat(socket.output()).startsWith(expectedHeader);
    }

    @Test
    void login_실패() {
        final String requestBody = "account=gugu&password=wrong";
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Content-Length: " + requestBody.getBytes(StandardCharsets.UTF_8).length + " ",
                "Content-Type: application/x-www-form-urlencoded ",
                "",
                requestBody);
        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket, sessionManager);

        processor.process(socket);

        final String expected = String.join("\r\n",
                "HTTP/1.1 302 Found ",
                "Location: /401.html ",
                "Content-Length: 0 ",
                "",
                "");

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void register_페이지() throws IOException, URISyntaxException {
        final String httpRequest = String.join("\r\n",
                "GET /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "",
                "");
        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket, sessionManager);

        processor.process(socket);

        final URL resource = getClass().getClassLoader().getResource("static/register.html");
        final byte[] body = Files.readAllBytes(Path.of(resource.toURI()));
        final String expectedHeader = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: " + body.length + " \r\n" +
                "\r\n";

        assertThat(socket.output()).startsWith(expectedHeader);
    }

    @Test
    void register_성공() {
        final String requestBody = "account=zeze&password=password&email=zeze%40woowahan.com";
        final String httpRequest = String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Content-Length: " + requestBody.getBytes(StandardCharsets.UTF_8).length + " ",
                "Content-Type: application/x-www-form-urlencoded ",
                "",
                requestBody);
        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket, sessionManager);

        processor.process(socket);

        final String expected = String.join("\r\n",
                "HTTP/1.1 302 Found ",
                "Location: /index.html ",
                "Content-Length: 0 ",
                "",
                "");

        assertThat(socket.output()).isEqualTo(expected);
        assertThat(InMemoryUserRepository.findByAccount("zeze")).isPresent();
    }
}
