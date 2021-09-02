package nextstep.jwp.framework.http.common;

import nextstep.jwp.application.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HttpSessionTest {

    private static final String TEST_SESSION_ID = "test_session_id";

    @DisplayName("SessionId로 HttpSession을 생성하면 HttpSessions에서 관리한다.")
    @Test
    void sessionCreate() {
        final HttpSession httpSession = HttpSession.of(TEST_SESSION_ID);
        assertThat(httpSession.getSessionId()).isEqualTo(TEST_SESSION_ID);

        final HttpSession findSession = HttpSessions.getSession(TEST_SESSION_ID);
        assertThat(httpSession).isEqualTo(findSession);
    }

    @DisplayName("Session에 필요한 정보를 저장할 수 있다.")
    @Test
    void sessionSave() {
        final HttpSession httpSession = HttpSession.of(TEST_SESSION_ID);
        final User user = new User("account", "password", "email");
        httpSession.setAttribute("user", user);

        final User findUser = (User) httpSession.getAttribute("user");
        assertThat(user).isEqualTo(findUser);
    }


    @DisplayName("Session에 저장한 정보를 삭제할 수 있다.")
    @Test
    void removeAttribute() {
        final HttpSession httpSession = HttpSession.of(TEST_SESSION_ID);
        final User user = new User("account", "password", "email");
        httpSession.setAttribute("user", user);
        httpSession.removeAttribute("user");

        final Object findAttribute = httpSession.getAttribute("user");
        assertThat(findAttribute).isNull();
    }

    @DisplayName("Session을 파기할 수 있다.")
    @Test
    void invalidate() {
        final HttpSession httpSession = HttpSession.of(TEST_SESSION_ID);
        assertThat(HttpSessions.getSession(TEST_SESSION_ID)).isNotNull();

        httpSession.invalidate();
        assertThat(HttpSessions.getSession(TEST_SESSION_ID)).isNull();
    }
}
