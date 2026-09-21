package org.apache.coyote.http11;

import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.catalina.controller.RequestMapping;
import org.apache.catalina.dispatcher.Dispatcher;
import org.apache.catalina.dispatcher.ViewResolver;
import org.apache.catalina.dispatcher.handler.ControllerHandler;
import org.apache.catalina.dispatcher.handler.HandlerMapping;
import org.apache.catalina.dispatcher.handler.StaticHandler;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;
import org.junit.jupiter.api.Test;
import support.StubSocket;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class Http11ProcessorTest {

    private final SessionManager sessionManager = new SessionManager();
    private final Dispatcher dispatcher = createDispatcher(sessionManager);

    // Application.main과 같은 구성
    private static Dispatcher createDispatcher(SessionManager sessionManager) {
        LoginController loginController = new LoginController(sessionManager);
        RequestMapping requestMapping = new RequestMapping(Map.of(
                "/login", loginController,
                "/login.html", loginController,
                "/register", new RegisterController()
        ));
        HandlerMapping handlerMapping = new HandlerMapping(List.of(
                new ControllerHandler(requestMapping),
                new StaticHandler()
        ));
        return new Dispatcher(handlerMapping, new ViewResolver());
    }

    @Test
    void 루트_경로로_요청하면_index_html을_응답한다() throws Exception {
        final var socket = new StubSocket("GET / HTTP/1.1\r\nHost: localhost:8080\r\n\r\n");
        final var processor = new Http11Processor(socket, dispatcher);

        processor.process(socket);

        final String content = readResource("static/index.html");
        final String expected = response("200 OK", "text/html", content);
        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void 로그인_경로로_요청하면_login_html을_응답한다() throws Exception {
        final var socket = new StubSocket("GET /login?account=gugu&password=password HTTP/1.1\r\nHost: localhost:8080\r\n\r\n");
        final var processor = new Http11Processor(socket, dispatcher);

        processor.process(socket);

        final String content = readResource("static/login.html");
        final String expected = response("200 OK", "text/html", content);
        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void 존재하지_않는_경로로_요청하면_404를_응답한다() throws Exception {
        final var socket = new StubSocket("GET /not-found HTTP/1.1\r\nHost: localhost:8080\r\n\r\n");
        final var processor = new Http11Processor(socket, dispatcher);

        processor.process(socket);

        final String content = readResource("static/404.html");
        final String expected = response("404 Not Found", "text/html", content);
        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void POST_회원가입_요청은_사용자를_저장하고_메인_페이지로_리다이렉트한다() {
        final String body = "account=new-user&password=new-password&email=new-user%40example.com";
        final var socket = new StubSocket(postRequest("/register", body));
        final var processor = new Http11Processor(socket, dispatcher);

        processor.process(socket);

        assertThat(socket.output()).isEqualTo(redirectResponse("/index.html"));
        assertThat(InMemoryUserRepository.findByAccount("new-user"))
                .hasValueSatisfying(user -> assertThat(user.checkPassword("new-password")).isTrue());
    }

    @Test
    void 로그인에_성공하면_사용자를_세션에_저장한다() throws Exception {
        final String body = "account=gugu&password=password";
        final var socket = new StubSocket(postRequest("/login", body));
        final var processor = new Http11Processor(socket, dispatcher);

        processor.process(socket);

        assertThat(socket.output())
                .startsWith("HTTP/1.1 302 FOUND\r\n")
                .contains("Location: /index.html\r\n")
                .contains("Set-Cookie: JSESSIONID=")
                .endsWith("\r\nContent-Length: 0\r\n");

        final String sessionId = headerValue(socket.output(), "Set-Cookie").substring("JSESSIONID=".length());
        final Session session = sessionManager.findSession(sessionId);
        assertThat(session).isNotNull();
        assertThat(session.getAttribute("user"))
                .isInstanceOfSatisfying(User.class, user -> assertThat(user.getAccount()).isEqualTo("gugu"));
    }

    @Test
    void 이미_로그인한_사용자가_GET_login을_요청하면_메인_페이지로_리다이렉트한다() {
        final String sessionId = "logged-in-session";
        final Session session = new Session(sessionId);
        session.setAttribute("user", new User("gugu", "password", "gugu@example.com"));
        sessionManager.add(session);
        final var socket = new StubSocket(String.join("\r\n",
                "GET /login HTTP/1.1",
                "Host: localhost:8080",
                "Cookie: JSESSIONID=" + sessionId,
                "",
                ""));
        final var processor = new Http11Processor(socket, dispatcher);

        processor.process(socket);

        assertThat(socket.output()).isEqualTo(redirectResponse("/index.html"));
    }

    @Test
    void 이미_로그인한_사용자가_GET_login_html을_요청하면_메인_페이지로_리다이렉트한다() {
        final String sessionId = "logged-in-session-html";
        final Session session = new Session(sessionId);
        session.setAttribute("user", new User("gugu", "password", "gugu@example.com"));
        sessionManager.add(session);
        final var socket = new StubSocket(String.join("\r\n",
                "GET /login.html HTTP/1.1",
                "Host: localhost:8080",
                "Cookie: JSESSIONID=" + sessionId,
                "",
                ""));
        final var processor = new Http11Processor(socket, dispatcher);

        processor.process(socket);

        assertThat(socket.output()).isEqualTo(redirectResponse("/index.html"));
    }

    @Test
    void 로그인하지_않은_사용자가_GET_login_html을_요청하면_login_html을_응답한다() throws Exception {
        final var socket = new StubSocket("GET /login.html HTTP/1.1\r\nHost: localhost:8080\r\n\r\n");
        final var processor = new Http11Processor(socket, dispatcher);

        processor.process(socket);

        final String content = readResource("static/login.html");
        assertThat(socket.output()).isEqualTo(response("200 OK", "text/html", content));
    }

    private String readResource(final String name) throws Exception {
        final URL resource = getClass().getClassLoader().getResource(name);
        assertThat(resource).isNotNull();
        return Files.readString(Path.of(resource.toURI()), StandardCharsets.UTF_8);
    }

    private String response(final String status, final String contentType, final String content) {
        return String.join("\r\n",
                "HTTP/1.1 " + status,
                "Content-Type: " + contentType + ";charset=utf-8",
                "Content-Length: " + content.getBytes(StandardCharsets.UTF_8).length,
                "",
                content);
    }

    private String postRequest(final String path, final String body) {
        return String.join("\r\n",
                "POST " + path + " HTTP/1.1",
                "Host: localhost:8080",
                "Content-Type: application/x-www-form-urlencoded",
                "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length,
                "",
                body);
    }

    private String redirectResponse(final String location) {
        return String.join("\r\n",
                "HTTP/1.1 302 FOUND",
                "Location: " + location,
                "Content-Length: 0",
                "");
    }

    private String headerValue(final String response, final String headerName) {
        final String prefix = headerName + ": ";
        return response.lines()
                .filter(line -> line.startsWith(prefix))
                .map(line -> line.substring(prefix.length()))
                .findFirst()
                .orElseThrow();
    }
}
