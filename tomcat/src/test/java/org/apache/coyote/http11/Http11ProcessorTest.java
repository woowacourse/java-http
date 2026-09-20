package org.apache.coyote.http11;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.techcourse.db.InMemoryUserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import support.StubSocket;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

class Http11ProcessorTest {

    @Test
    @DisplayName("/ 요청을 index.html 정적 리소스로 응답한다")
    void processTest() throws IOException {
        // when
        String response = process("/");

        // then
        assertThat(response).isEqualTo(expectedResponse("/index.html", "text/html;charset=utf-8"));
    }

    @Test
    @DisplayName("index.html 정적 리소스를 응답한다")
    void index() throws IOException {
        // when
        String response = process("/index.html");

        // then
        assertThat(response).isEqualTo(expectedResponse("/index.html", "text/html;charset=utf-8"));
    }

    @Test
    @DisplayName("확장자가 없는 HTML 경로는 .html을 붙여 응답한다")
    void extensionlessHtmlPath() throws IOException {
        // when
        String response = process("/login");

        // then
        assertThat(response).isEqualTo(expectedResponse("/login.html", "text/html;charset=utf-8"));
    }

    @Test
    @DisplayName("html은 text_html로 응답한다")
    void htmlTest() {
        // when
        String response = process("/index.html");

        // then
        assertThat(response)
                .contains("Content-Type: text/html;charset=utf-8");
    }

    @Test
    @DisplayName("css text_css로 응답한다")
    void css() throws IOException {
        // when
        String response = process("/css/styles.css");

        // then
        assertThat(response).isEqualTo(expectedResponse("/css/styles.css", "text/css;charset=utf-8"));
    }

    @Test
    @DisplayName("쿼리 스트링이 포함된 요청에서 경로에 해당하는 HTML을 응답한다")
    void queryString() throws IOException {
        // when
        String response = process("/login.html?account=gugu&password=password");

        // then
        assertThat(response).isEqualTo(expectedResponse("/login.html", "text/html;charset=utf-8"));
    }

    @Test
    @DisplayName("로그인 요청에서 account와 password가 일치하면 사용자를 로그로 출력한다")
    void loginSuccess() {
        // when
        List<String> logs = loggedMessages(() -> process("/login.html?account=gugu&password=password"));

        // then
        assertThat(logs)
                .contains("조회된 사용자: id=1, account=gugu");
    }

    @Test
    @DisplayName("로그인 요청에서 password가 일치하지 않으면 사용자를 로그로 출력하지 않는다")
    void loginWithWrongPassword() {
        // when
        List<String> logs = loggedMessages(() -> process("/login.html?account=gugu&password=wrong"));

        // then
        assertThat(logs)
                .doesNotContain("조회된 사용자: id=1, account=gugu");
    }

    @Test
    @DisplayName("로그인 경로가 아니면 query string이 있어도 사용자를 로그로 출력하지 않는다")
    void queryStringOnNonLoginPath() {
        // when
        List<String> logs = loggedMessages(() -> process("/index.html?account=gugu&password=password"));

        // then
        assertThat(logs)
                .doesNotContain("조회된 사용자: id=1, account=gugu");
    }

    @Test
    @DisplayName("잘못된 query string은 조회하지 않고 요청한 HTML을 응답한다")
    void malformedQueryString() throws IOException {
        // when
        String response = process("/login.html?account");

        // then
        assertThat(response).isEqualTo(expectedResponse("/login.html", "text/html;charset=utf-8"));
    }

    @Test
    @DisplayName("정적 리소스가 없으면 404.html을 응답한다")
    void notFound() throws IOException {
        // when
        String response = process("/unknown.html");

        // then
        assertThat(response).isEqualTo(expectedResponse("404 Not Found", "/404.html", "text/html;charset=utf-8"));
    }

    @Test
    @DisplayName("로그인에 성공하면 index.html로 리다이렉트한다")
    void postLoginSuccess() {
        // when
        String response = process("POST", "/login", "account=gugu&password=password");

        // then
        assertThat(response)
                .startsWith("HTTP/1.1 302 Found\r\n")
                .contains("Location: /index.html");
    }

    @Test
    @DisplayName("로그인에 실패하면 401.html로 리다이렉트한다")
    void postLoginFailure() {
        // when
        String response = process("POST", "/login", "account=gugu&password=wrong");

        // then
        assertThat(response)
                .startsWith("HTTP/1.1 302 Found\r\n")
                .contains("Location: /401.html");
    }

    @Test
    @DisplayName("로그인 폼은 POST로 전송한다")
    void loginFormMethod() {
        // when
        String response = process("/login");

        // then
        assertThat(response)
                .contains("<form method=\"post\" action=\"login\">");
    }

    @Test
    @DisplayName("회원가입을 완료하면 사용자를 저장하고 index.html로 리다이렉트한다")
    void postRegister() {
        // when
        String response = process("POST", "/register", "account=roro&password=java&email=roro%40example.com");

        // then
        assertThat(response)
                .startsWith("HTTP/1.1 302 Found\r\n")
                .contains("Location: /index.html");
        assertThat(InMemoryUserRepository.findByAccount("roro"))
                .isPresent();
    }

    private String process(String path) {
        final var socket = new StubSocket(httpRequest(path));
        final var processor = new Http11Processor(socket);

        processor.process(socket);

        return socket.output();
    }

    private String process(String method, String path, String body) {
        final var socket = new StubSocket(httpRequest(method, path, body));
        final var processor = new Http11Processor(socket);

        processor.process(socket);

        return socket.output();
    }

    private String httpRequest(String path) {
        return String.join("\r\n",
                "GET " + path + " HTTP/1.1 ",
                "Host: localhost:8080 ",
                "",
                "");
    }

    private String httpRequest(String method, String path, String body) {
        return String.join("\r\n",
                method + " " + path + " HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Content-Type: application/x-www-form-urlencoded",
                "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length,
                "",
                body);
    }

    private String expectedResponse(String path, String contentType) throws IOException {
        return expectedResponse("200 OK", path, contentType);
    }

    private String expectedResponse(String status, String path, String contentType) throws IOException {
        byte[] body = readResource(path);

        return "HTTP/1.1 " + status + "\r\n"
                + "Content-Type: " + contentType + "\r\n"
                + "Content-Length: " + body.length + "\r\n"
                + "\r\n"
                + new String(body, StandardCharsets.UTF_8);
    }

    private byte[] readResource(String path) throws IOException {
        URL resource = getClass()
                .getClassLoader()
                .getResource("static" + path);

        return Files.readAllBytes(new File(Objects.requireNonNull(resource).getFile()).toPath());
    }

    private List<String> loggedMessages(Runnable action) {
        Logger logger = (Logger) LoggerFactory.getLogger(Http11Processor.class);
        ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.start();
        logger.addAppender(appender);

        try {
            action.run();
            return appender.list.stream()
                    .map(ILoggingEvent::getFormattedMessage)
                    .toList();
        } finally {
            logger.detachAppender(appender);
        }
    }

}
