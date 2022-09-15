package nextstep.org.apache.coyote.http11.session;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import nextstep.jwp.model.User;
import org.apache.coyote.http11.exception.SessionKeyNotFoundException;
import org.apache.coyote.http11.session.Session;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SessionTest {

    @DisplayName("session 값을 설정한다")
    @Test
    void setAttribute() {
        final Session session = new Session("user");
        final User user = new User("slow", "1234", "email");
        session.setAttribute("user", user);

        final User actual = (User) session.getAttribute("user");

        assertThat(actual).isEqualTo(user);
    }

    @DisplayName("session 값이 없는 경우, 예외를 발생한다")
    @Test
    void noSessionException() {
        final Session session = new Session("user");
        assertThatThrownBy(() -> session.getAttribute("user2"))
                .isInstanceOf(SessionKeyNotFoundException.class)
                .hasMessageContaining("존재하지 않는 세션 Attribute 입니다.");
    }
}
