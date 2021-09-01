package nextstep.jwp.http;

import static org.assertj.core.api.Assertions.assertThat;

import nextstep.jwp.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpSessionTest {

    @DisplayName("유저 정보를 세션에 등록한다.")
    @Test
    void create() {
        User user = new User(100L, "user", "password", "email");
        HttpSession httpSession = new HttpSession(user);

        assertThat(httpSession.getUser()).isEqualTo(user);
        assertThat(HttpSessions.getSession(httpSession.getId())).isEqualTo(httpSession);
    }

    @DisplayName("세션을 무효화 시킨다.")
    @Test
    void invalidate() {
        User user = new User(100L, "user", "password", "email");
        HttpSession httpSession = new HttpSession(user);
        httpSession.invalidate();

        assertThat(HttpSessions.getSession(httpSession.getId())).isNull();
    }
}