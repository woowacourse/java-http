package nextstep.jwp.server.http;

import nextstep.jwp.server.http.common.HttpSession;
import nextstep.jwp.server.http.common.HttpSessions;
import nextstep.jwp.web.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HttpSessionTest {

    @Test
    @DisplayName("세션에 키와 값을 추가하고 통일한 키로 값을 가져온다.")
    void setAndGetAttribute() {
        // given
        HttpSession httpSession = new HttpSession("session");
        User user = new User(1L, "oz", "123", "oz@oz");

        // when
        httpSession.setAttribute("user", user);

        // then
        assertThat((User) httpSession.getAttribute("user")).isEqualTo(user);
    }

    @Test
    @DisplayName("세션을 관리하는 HttpSessions에 id를 키로 세션을 정리하고 동일한 세션을 id로 가져온다.")
    void addAndFindSession() {
        // given
        String id = "session";
        HttpSession httpSession = new HttpSession(id);
        HttpSessions.addSession(id, httpSession);

        // when
        HttpSession findSession = HttpSessions.findSession(id);

        // then
        assertThat(findSession).isEqualTo(httpSession);
    }
}
