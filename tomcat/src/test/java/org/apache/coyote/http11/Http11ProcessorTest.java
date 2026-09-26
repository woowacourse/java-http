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
        final var expected = String.join("\r\n",
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
        final String httpRequest = String.join("\r\n",
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
        final byte[] body = readStaticFile("static/index.html");
        final var expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: " + body.length + " \r\n" +
                "\r\n" +
                new String(body, StandardCharsets.UTF_8);

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
        final byte[] body = readStaticFile("static/css/styles.css");
        final var expected = "HTTP/1.1 200 OK \r\n" +
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
        final byte[] body = readStaticFile("static/login.html");
        final String expectedHeader = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: " + body.length + " \r\n" +
                "\r\n";

        assertThat(socket.output()).startsWith(expectedHeader);
    }

    @Test
    void login_성공() {
        // given
        final var socket = new StubSocket(formPost("/login", "account=gugu&password=password"));
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
        final var loginSocket = new StubSocket(formPost("/login", "account=gugu&password=password"));
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
        final byte[] body = readStaticFile("static/login.html");
        final String expectedHeader = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: " + body.length + " \r\n" +
                "\r\n";

        assertThat(socket.output()).startsWith(expectedHeader);
    }

    @Test
    void login_실패() {
        // given
        final var socket = new StubSocket(formPost("/login", "account=gugu&password=wrong"));
        final Http11Processor processor = new Http11Processor(socket, sessionManager);

        // when
        processor.process(socket);

        // then
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
        // given
        final String httpRequest = String.join("\r\n",
                "GET /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "",
                "");
        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket, sessionManager);

        // when
        processor.process(socket);

        // then
        final byte[] body = readStaticFile("static/register.html");
        final String expectedHeader = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: " + body.length + " \r\n" +
                "\r\n";

        assertThat(socket.output()).startsWith(expectedHeader);
    }

    @Test
    void register_성공() {
        // given
        final var socket = new StubSocket(
                formPost("/register", "account=zeze&password=password&email=zeze%40woowahan.com"));
        final Http11Processor processor = new Http11Processor(socket, sessionManager);

        // when
        processor.process(socket);

        // then
        final String expected = String.join("\r\n",
                "HTTP/1.1 302 Found ",
                "Location: /index.html ",
                "Content-Length: 0 ",
                "",
                "");

        assertThat(socket.output()).isEqualTo(expected);
        assertThat(InMemoryUserRepository.findByAccount("zeze")).isPresent();
    }

    @Test
    void 인코딩된_한글_계정으로_회원가입한다() {
        // given
        final String encodedAccount = "%EB%8B%AC%EC%88%98";   // "달수"
        final var socket = new StubSocket(formPost("/register",
                "account=" + encodedAccount + "&password=password&email=dalsu%40woowahan.com"));
        final Http11Processor processor = new Http11Processor(socket, sessionManager);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).startsWith("HTTP/1.1 302 Found \r\nLocation: /index.html ");
        assertThat(InMemoryUserRepository.findByAccount("달수")).isPresent();
    }

    @Test
    void 인코딩되지_않은_한글_본문은_거부한다() {
        // given
        final var socket = new StubSocket(formPost("/register",
                "account=달수&password=password&email=dalsu%40woowahan.com"));
        final Http11Processor processor = new Http11Processor(socket, sessionManager);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).startsWith("HTTP/1.1 400 Bad Request ");
    }

    @Test
    void 지원하지_않는_메서드는_405와_Allow를_응답한다() {
        // given
        final String httpRequest = String.join("\r\n",
                "PUT /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "",
                "");
        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket, sessionManager);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).startsWith("HTTP/1.1 405 Method Not Allowed ");
        assertThat(socket.output()).contains("Allow: GET, HEAD, POST ");
    }

    private static String formPost(final String path, final String body) {
        return String.join("\r\n",
                "POST " + path + " HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length + " ",
                "Content-Type: application/x-www-form-urlencoded ",
                "",
                body);
    }

    private byte[] readStaticFile(final String path) throws IOException, URISyntaxException {
        final URL resource = getClass().getClassLoader().getResource(path);
        return Files.readAllBytes(Path.of(resource.toURI()));
    }
}
