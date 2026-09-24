package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.controller.RequestMapping;
import org.junit.jupiter.api.Test;
import support.StubSocket;

class Http11ProcessorTest {

    @Test
    void process() {
        // given
        final StubSocket socket = new StubSocket();
        final Http11Processor processor = createProcessor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output())
                .contains("HTTP/1.1 200 OK")
                .contains("content-type: text/html;charset=utf-8")
                .contains("Hello world!");
    }

    @Test
    void index() throws IOException {
        // given
        final String httpRequest = String.join(
                "\r\n",
                "GET /index.html HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "",
                ""
        );

        final StubSocket socket = new StubSocket(httpRequest);
        final Http11Processor processor = createProcessor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass()
                .getClassLoader()
                .getResource("static/index.html");

        final String responseBody = new String(
                Files.readAllBytes(
                        new File(resource.getFile()).toPath()
                )
        );

        assertThat(socket.output())
                .contains("HTTP/1.1 200 OK")
                .contains("set-cookie: JSESSIONID=")
                .contains("content-type: text/html;charset=utf-8")
                .contains("content-length: 5564")
                .contains(responseBody);
    }

    @Test
    void login_success() {
        // given
        final String body =
                "account=gugu&password=password";

        final String httpRequest = String.join(
                "\r\n",
                "POST /login HTTP/1.1",
                "Content-Length: " + body.length(),
                "Content-Type: application/x-www-form-urlencoded",
                "Connection: keep-alive",
                "",
                body
        );

        final StubSocket socket = new StubSocket(httpRequest);
        final Http11Processor processor = createProcessor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output())
                .contains("HTTP/1.1 302 Found")
                .contains("location: /index.html");
    }

    @Test
    void login_failure() {
        // given
        final String body =
                "account=gugu&password=gugu";

        final String httpRequest = String.join(
                "\r\n",
                "POST /login HTTP/1.1",
                "Content-Length: " + body.length(),
                "Content-Type: application/x-www-form-urlencoded",
                "Connection: keep-alive",
                "",
                body
        );

        final StubSocket socket = new StubSocket(httpRequest);
        final Http11Processor processor = createProcessor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output())
                .contains("HTTP/1.1 302 Found")
                .contains("location: /401.html");
    }

    @Test
    void register_success() {
        // given
        final String body =
                "account=pobi&password=1234&email=pobi@test.com";

        final String httpRequest = String.join(
                "\r\n",
                "POST /register HTTP/1.1",
                "Content-Length: " + body.length(),
                "Content-Type: application/x-www-form-urlencoded",
                "",
                body
        );

        final StubSocket socket = new StubSocket(httpRequest);
        final Http11Processor processor = createProcessor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output())
                .contains("HTTP/1.1 302 Found")
                .contains("location: /index.html");

        assertThat(
                InMemoryUserRepository.findByAccount("pobi")
        ).isPresent();
    }

    @Test
    void jSessionIdIssue_success() {
        // given
        final String httpRequest = String.join(
                "\r\n",
                "GET /index.html HTTP/1.1",
                "Host: localhost:8080",
                "",
                ""
        );

        final StubSocket socket = new StubSocket(httpRequest);
        final Http11Processor processor = createProcessor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output())
                .contains("set-cookie: JSESSIONID=");
    }

    @Test
    void jSessionIdIssue_success_already_have_no_issue() {
        // given
        final String sessionId = "existing-id";

        final Session session = new Session(sessionId);
        SessionManager.getInstance().add(session);

        final String httpRequest = String.join(
                "\r\n",
                "GET /index.html HTTP/1.1",
                "Cookie: JSESSIONID=" + sessionId,
                "",
                ""
        );

        final StubSocket socket = new StubSocket(httpRequest);
        final Http11Processor processor = createProcessor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output())
                .doesNotContain("set-cookie: JSESSIONID=");
    }

    @Test
    void login_success_save_user_in_session() {
        // given
        final String sessionId = "login-session-id";
        final String body =
                "account=gugu&password=password";

        final SessionManager sessionManager =
                SessionManager.getInstance();

        final Session session = new Session(sessionId);
        sessionManager.add(session);

        final String httpRequest = String.join(
                "\r\n",
                "POST /login HTTP/1.1",
                "Cookie: JSESSIONID=" + sessionId,
                "Content-Length: " + body.length(),
                "Content-Type: application/x-www-form-urlencoded",
                "",
                body
        );

        final StubSocket socket = new StubSocket(httpRequest);
        final Http11Processor processor = createProcessor(socket);

        // when
        processor.process(socket);

        // then
        final Session savedSession =
                sessionManager.findSession(sessionId);

        final User user = (User) savedSession
                .getAttribute("user");

        assertThat(user).isNotNull();
        assertThat(user.getAccount()).isEqualTo("gugu");

        assertThat(socket.output())
                .contains("HTTP/1.1 302 Found")
                .contains("location: /index.html");
    }

    @Test
    void logged_in_user_access_login_redirect_index() {
        // given
        final String sessionId = "login-session";

        final Session session = new Session(sessionId);

        final User user = new User(
                "gugu",
                "password",
                "gugu@email.com"
        );

        session.setAttribute("user", user);

        SessionManager.getInstance()
                .add(session);

        final String httpRequest = String.join(
                "\r\n",
                "GET /login HTTP/1.1",
                "Cookie: JSESSIONID=" + sessionId,
                "",
                ""
        );

        final StubSocket socket = new StubSocket(httpRequest);
        final Http11Processor processor = createProcessor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output())
                .contains("HTTP/1.1 302 Found")
                .contains("location: /index.html");
    }

    @Test
    void not_logged_in_user_access_login_response_login_page()
            throws IOException {
        // given
        final String httpRequest = String.join(
                "\r\n",
                "GET /login HTTP/1.1",
                "",
                ""
        );

        final StubSocket socket = new StubSocket(httpRequest);
        final Http11Processor processor = createProcessor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass()
                .getClassLoader()
                .getResource("static/login.html");

        final String responseBody = new String(
                Files.readAllBytes(
                        new File(resource.getFile()).toPath()
                )
        );

        assertThat(socket.output())
                .contains("HTTP/1.1 200 OK")
                .contains(responseBody);
    }

    private Http11Processor createProcessor(
            final StubSocket socket
    ) {
        final RequestMapping requestMapping =
                new RequestMapping();

        requestMapping.addController(
                "/login",
                new LoginController()
        );

        requestMapping.addController(
                "/register",
                new RegisterController()
        );

        return new Http11Processor(
                socket,
                requestMapping
        );
    }
}
