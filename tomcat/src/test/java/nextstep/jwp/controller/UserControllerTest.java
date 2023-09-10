package nextstep.jwp.controller;

import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;

import nextstep.org.apache.coyote.http11.HttpServletRequestFixture;
import org.apache.catalina.core.servlet.HttpServletResponse;
import org.apache.catalina.core.session.SessionManager;
import org.apache.catalina.core.session.SessionManager.Session;
import org.apache.coyote.http11.common.Status;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserControllerTest {

    private static final SessionManager sessionManager = new SessionManager();

    @DisplayName("로그인에 성공하면 index.html로 리다이렉트한다.")
    @Test
    void loginSuccess() {
        final Session session = sessionManager.findOrCreate(randomUUID().toString());
        final var httpServletRequest = HttpServletRequestFixture.createPost(
                "/login", session.getId(), "account=gugu&password=password"
        );

        final HttpServletResponse response = UserController.login(httpServletRequest);

        assertThat(response.getStatus()).isEqualTo(Status.FOUND);
        assertThat(response.getLocation()).isEqualTo("/index.html");
    }

    @DisplayName("로그인에 실패하면 401.html로 리다이렉트한다.")
    @Test
    void loginFail() {
        final Session session = sessionManager.findOrCreate(randomUUID().toString());
        final var httpServletRequest = HttpServletRequestFixture.createPost(
                "/login", session.getId(), "account=dodo&password=password"
        );

        final HttpServletResponse response = UserController.login(httpServletRequest);

        assertThat(response.getStatus()).isEqualTo(Status.FOUND);
        assertThat(response.getLocation()).isEqualTo("/401.html");
    }
}
