package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import com.techcourse.model.User;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import org.apache.catalina.Manager;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.junit.jupiter.api.Test;
import support.StubSocket;

class Step2Test {

    @Test
    void POST_로그인은_성공과_실패에_맞는_경로로_리다이렉트한다() {
        Manager manager = new SessionManager();

        StubSocket success = loginRequest(
                "gugu",
                "password"
        );
        process(success, manager);

        assertThat(success.output())
                .startsWith("HTTP/1.1 302");
        assertThat(success.output())
                .contains("Location: /index.html");

        StubSocket unknownAccount = loginRequest(
                "unknown",
                "password"
        );
        process(unknownAccount, manager);

        assertThat(unknownAccount.output())
                .contains("Location: /401.html");

        StubSocket wrongPassword = loginRequest(
                "gugu",
                "wrong"
        );
        process(wrongPassword, manager);

        assertThat(wrongPassword.output())
                .contains("Location: /401.html");

        String getRequest = String.join("\r\n",
                "GET /login?account=gugu&password=password HTTP/1.1",
                "Host: localhost:8080",
                "",
                ""
        );
        StubSocket getSocket = new StubSocket(getRequest);

        process(getSocket, manager);

        assertThat(getSocket.output())
                .startsWith("HTTP/1.1 200");
        assertThat(getSocket.output())
                .doesNotContain("Location:");
    }

    @Test
    void POST로_회원가입하면_저장된_계정으로_로그인할_수_있다() {
        Manager manager = new SessionManager();

        String account = "user-" + UUID.randomUUID();

        String registerBody =
                "account=" + account
                        + "&email=user%40example.com"
                        + "&password=password";

        String registerRequest = String.join("\r\n",
                "POST /register HTTP/1.1",
                "Content-Length: "
                        + registerBody.getBytes(StandardCharsets.UTF_8).length,
                "Content-Type: application/x-www-form-urlencoded",
                "",
                registerBody
        );

        StubSocket registerSocket = new StubSocket(registerRequest);

        process(registerSocket, manager);

        assertThat(registerSocket.output())
                .startsWith("HTTP/1.1 302");
        assertThat(registerSocket.output())
                .contains("Location: /index.html");

        StubSocket loginSocket = loginRequest(account, "password");

        process(loginSocket, manager);

        assertThat(loginSocket.output())
                .contains("Location: /index.html");
    }

    @Test
    void JSESSIONID가_없거나_유효하지_않으면_새_세션을_발급하고_유효하면_재사용한다() {
        Manager manager = new SessionManager();

        String requestWithoutSession = String.join("\r\n",
                "GET /index.html HTTP/1.1",
                "Host: localhost:8080",
                "",
                ""
        );

        StubSocket firstSocket = new StubSocket(requestWithoutSession);

        process(firstSocket, manager);

        String sessionId = sessionIdFrom(firstSocket);

        assertThat(manager.findSession(sessionId))
                .isPresent();

        String validSessionRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1",
                "Cookie: JSESSIONID=" + sessionId,
                "",
                ""
        );

        StubSocket validSocket = new StubSocket(validSessionRequest);

        process(validSocket, manager);

        assertThat(validSocket.output())
                .doesNotContain("Set-Cookie: JSESSIONID=");

        String unknownId = UUID.randomUUID().toString();

        String invalidSessionRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1",
                "Cookie: JSESSIONID=" + unknownId,
                "",
                ""
        );

        StubSocket invalidSocket = new StubSocket(invalidSessionRequest);

        process(invalidSocket, manager);

        String newSessionId = sessionIdFrom(invalidSocket);

        assertThat(newSessionId)
                .isNotEqualTo(unknownId);

        assertThat(manager.findSession(newSessionId))
                .isPresent();
    }

    @Test
    void 로그인한_세션으로_로그인_페이지에_접근하면_홈으로_리다이렉트한다() {
        Manager manager = new SessionManager();

        String pageRequest = String.join("\r\n",
                "GET /login HTTP/1.1",
                "",
                ""
        );

        StubSocket pageSocket = new StubSocket(pageRequest);

        process(pageSocket, manager);

        assertThat(pageSocket.output())
                .startsWith("HTTP/1.1 200");

        String initialSessionId = sessionIdFrom(pageSocket);

        String body = "account=gugu&password=password";

        String loginRequest = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Cookie: JSESSIONID=" + initialSessionId,
                "Content-Length: "
                        + body.getBytes(StandardCharsets.UTF_8).length,
                "Content-Type: application/x-www-form-urlencoded",
                "",
                body
        );

        StubSocket loginSocket = new StubSocket(loginRequest);

        process(loginSocket, manager);

        String loggedInSessionId = sessionIdFrom(loginSocket);

        assertThat(loggedInSessionId).isNotEqualTo(initialSessionId);
        assertThat(manager.findSession(initialSessionId)).isEmpty();

        Session session = manager
                .findSession(loggedInSessionId)
                .orElseThrow();

        User user = (User) session.getAttribute("user");

        assertThat(user.getAccount())
                .isEqualTo("gugu");

        String authenticatedRequest = String.join("\r\n",
                "GET /login HTTP/1.1",
                "Cookie: JSESSIONID=" + loggedInSessionId,
                "",
                ""
        );

        StubSocket authenticatedSocket = new StubSocket(authenticatedRequest);

        process(authenticatedSocket, manager);

        assertThat(authenticatedSocket.output())
                .startsWith("HTTP/1.1 302");

        assertThat(authenticatedSocket.output())
                .contains("Location: /index.html");
    }

    @Test
    void 쿠키_없이_바로_로그인하면_최종_세션의_ID를_응답한다() {
        Manager manager = new SessionManager();
        StubSocket loginSocket = loginRequest("gugu", "password");

        process(loginSocket, manager);

        String sessionId = sessionIdFrom(loginSocket);
        Session session = manager.findSession(sessionId).orElseThrow();

        assertThat(((User) session.getAttribute("user")).getAccount())
                .isEqualTo("gugu");
        assertThat(loginSocket.output())
                .contains("Set-Cookie: JSESSIONID=" + sessionId + "; Path=/; HttpOnly");
    }

    @Test
    void 로그인에_실패하면_기존_세션을_유지한다() {
        Manager manager = new SessionManager();
        Session session = manager.createSession();
        String body = "account=gugu&password=wrong";
        String request = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Cookie: JSESSIONID=" + session.getId(),
                "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length,
                "Content-Type: application/x-www-form-urlencoded",
                "",
                body
        );
        StubSocket socket = new StubSocket(request);

        process(socket, manager);

        assertThat(socket.output()).contains("Location: /401.html");
        assertThat(socket.output()).doesNotContain("Set-Cookie: JSESSIONID=");
        assertThat(manager.findSession(session.getId())).contains(session);
    }

    @Test
    void 세션_쿠키를_발급할_때_다른_쿠키를_보존한다() {
        Manager manager = new SessionManager();
        RequestMapping mapping = new RequestMapping((request, response) ->
                response.addHeader("Set-Cookie", "theme=dark"));
        StubSocket socket = new StubSocket("GET / HTTP/1.1\r\n\r\n");

        new Http11Processor(socket, manager, mapping).process(socket);

        assertThat(socket.output())
                .contains("Set-Cookie: theme=dark\r\n")
                .contains("Set-Cookie: JSESSIONID=");
        assertThat(manager.findSession(sessionIdFrom(socket))).isPresent();
    }

    private StubSocket loginRequest(
            final String account,
            final String password
    ) {
        String body = "account=" + account + "&password=" + password;

        String request = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Content-Length: "
                        + body.getBytes(StandardCharsets.UTF_8).length,
                "Content-Type: application/x-www-form-urlencoded",
                "",
                body
        );

        return new StubSocket(request);
    }

    private void process(
            final StubSocket socket,
            final Manager manager
    ) {
        StaticResourceRenderer resourceRenderer = new StaticResourceRenderer();
        StaticResourceController staticResourceController =
                new StaticResourceController(resourceRenderer);
        RequestMapping requestMapping = new RequestMapping(staticResourceController);
        requestMapping.register(
                "/register",
                new RegisterController(resourceRenderer)
        );

        requestMapping.register(
                "/login",
                new LoginController(resourceRenderer)
        );
        new Http11Processor(socket, manager, requestMapping)
                .process(socket);
    }

    private String sessionIdFrom(
            final StubSocket socket
    ) {
        String cookieHeader = socket.output()
                .lines()
                .filter(line ->
                        line.startsWith("Set-Cookie: JSESSIONID="))
                .findFirst()
                .orElseThrow();

        return cookieHeader.substring("Set-Cookie: JSESSIONID=".length())
                .split(";", 2)[0];
    }
}
