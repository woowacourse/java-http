package nextstep.jwp.http.session;

import nextstep.jwp.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class HttpSessionTest {

    public static final User USER = new User("account", "password", "email@email.com");

    @DisplayName("session의 attribute를 지운다.")
    @Test
    void removeAttribute() {
        // given
        HttpSession session = new HttpSession("sessionId");
        session.setAttribute("user", USER);

        // when
        session.removeAttribute("user");

        // then
        assertThat(session.getAttribute("user")).isNull();
    }

    @DisplayName("session을 무효화 시킨다.")
    @Test
    void invalidate() {
        // given
        HttpSession session = new HttpSession("sessionId");
        session.setAttribute("user", USER);
        session.setAttribute("member", USER);
        HttpSessions.put(session);

        // when
        session.invalidate();

        // then
        assertThat(session.getAttribute("user")).isNull();
        assertThat(session.getAttribute("member")).isNull();
    }
}