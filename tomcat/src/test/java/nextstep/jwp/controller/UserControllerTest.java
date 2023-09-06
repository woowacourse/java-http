package nextstep.jwp.controller;

import static java.util.UUID.randomUUID;
import static org.apache.coyote.http11.common.header.HeaderName.COOKIE;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.apache.coyote.http11.SessionManager;
import org.apache.coyote.http11.SessionManager.Session;
import org.apache.coyote.http11.common.Cookies;
import org.apache.coyote.http11.common.Status;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserControllerTest {

    private static final SessionManager sessionManager = new SessionManager();

    @DisplayName("로그인에 성공하면 index.html로 리다이렉트한다.")
    @Test
    void loginSuccess() {
        final Session session = sessionManager.findOrCreate(randomUUID().toString());

        final Response response = UserController.login(
                Request.of("post", "/login",
                        Map.of(COOKIE, Cookies.ofJSessionId(session.getId())),
                        "account=gugu&password=password"));

        assertThat(response.getStatus()).isEqualTo(Status.FOUND);
        assertThat(response.getLocation()).isEqualTo("/index.html");
    }

    @DisplayName("로그인에 실패하면 401.html로 리다이렉트한다.")
    @Test
    void loginFail() {
        final Session session = sessionManager.findOrCreate(randomUUID().toString());

        final Response response = UserController.login(
                Request.of("post", "/login",
                        Map.of(COOKIE, Cookies.ofJSessionId(session.getId())),
                        "account=dodo&password=password"));

        assertThat(response.getStatus()).isEqualTo(Status.FOUND);
        assertThat(response.getLocation()).isEqualTo("/401.html");
    }
}
