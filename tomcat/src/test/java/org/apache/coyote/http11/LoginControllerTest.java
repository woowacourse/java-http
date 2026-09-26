package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import org.apache.catalina.Manager;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.junit.jupiter.api.Test;

class LoginControllerTest {

    @Test
    void 로그인하지_않은_GET_요청에_로그인_페이지를_응답한다() throws Exception {
        Manager manager = new SessionManager();
        Controller controller = new LoginController(manager, new StaticResourceController());
        HttpRequest request = request("GET /login HTTP/1.1\r\n\r\n");
        request.attachSession(manager.createSession());
        HttpResponse response = new HttpResponse();

        controller.service(request, response);

        String actual = responseText(response);

        assertThat(actual).contains("Content-Type: text/html");
        assertThat(actual).doesNotContain("Location: /index.html");
    }

    @Test
    void 로그인한_사용자의_GET_요청은_index로_리다이렉트한다() throws Exception {
        Manager manager = new SessionManager();
        Controller controller = new LoginController(manager, new StaticResourceController());

        Session session = manager.createSession();
        session.setAttribute(
                "user",
                new User("test", "password", "test@test.com")
        );

        HttpRequest request = request("GET /login HTTP/1.1\r\n\r\n");
        request.attachSession(session);
        HttpResponse response = new HttpResponse();

        controller.service(request, response);

        assertThat(responseText(response))
                .contains("Location: /index.html");
    }

    @Test
    void 올바른_로그인_정보로_요청하면_세션을_갱신하고_사용자를_저장한다() throws Exception {
        Manager manager = new SessionManager();
        Controller controller = new LoginController(manager, new StaticResourceController());

        User user = new User(
                "login-test",
                "password",
                "login-test@test.com"
        );
        InMemoryUserRepository.save(user);

        Session oldSession = manager.createSession();

        HttpRequest request = loginRequest(
                "login-test",
                "password"
        );
        request.attachSession(oldSession);

        HttpResponse response = new HttpResponse();

        controller.service(request, response);

        String actual = responseText(response);
        String renewedSessionId = sessionIdFrom(actual);

        assertThat(manager.findSession(oldSession.getId()))
                .isEmpty();

        Session renewedSession = manager.findSession(renewedSessionId)
                .orElseThrow();

        assertThat(renewedSession.getAttribute("user"))
                .isSameAs(user);

        assertThat(actual)
                .contains("Location: /index.html");
        assertThat(actual)
                .contains("Set-Cookie: JSESSIONID=" + renewedSessionId);
    }

    @Test
    void 잘못된_로그인_정보로_요청하면_401로_리다이렉트한다() throws Exception {
        Manager manager = new SessionManager();
        Controller controller = new LoginController(manager, new StaticResourceController());

        Session session = manager.createSession();

        HttpRequest request = loginRequest(
                "unknown",
                "wrong-password"
        );
        request.attachSession(session);

        HttpResponse response = new HttpResponse();

        controller.service(request, response);

        assertThat(responseText(response))
                .contains("Location: /401.html");
    }

    private HttpRequest loginRequest(
            final String account,
            final String password
    ) throws Exception {
        String body = "account=" + account + "&password=" + password;

        return request(
                String.join("\r\n",
                        "POST /login HTTP/1.1",
                        "Content-Type: application/x-www-form-urlencoded",
                        "Content-Length: "
                                + body.getBytes(StandardCharsets.UTF_8).length,
                        "",
                        body
                )
        );
    }

    private HttpRequest request(final String value) throws Exception {
        return HttpRequest.readFrom(
                new ByteArrayInputStream(
                        value.getBytes(StandardCharsets.UTF_8)
                )
        );
    }

    private String responseText(final HttpResponse response) {
        return new String(
                response.toByteArray(),
                StandardCharsets.UTF_8
        );
    }

    private String sessionIdFrom(final String response) {
        return Arrays.stream(response.split("\r\n"))
                .filter(line -> line.startsWith("Set-Cookie: JSESSIONID="))
                .map(line -> line.substring("Set-Cookie: JSESSIONID=".length()))
                .findFirst()
                .orElseThrow();
    }
}
