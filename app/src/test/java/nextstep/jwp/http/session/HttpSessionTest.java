package nextstep.jwp.http.session;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class HttpSessionTest {

    @DisplayName("Session의 아이디를 얻어온다.")
    @Test
    void getId() {
        String uuid = UUID.randomUUID().toString();
        HttpSession httpSession = new HttpSession(uuid);
        assertThat(httpSession.getId()).isEqualTo(uuid);
    }

    @DisplayName("Session에 속성값을 저장한다.")
    @Test
    void setAttribute() {
        String uuid = UUID.randomUUID().toString();
        HttpSession httpSession = new HttpSession(uuid);

        httpSession.setAttribute("user", "test");
        assertThat(httpSession.getAttribute("user")).isEqualTo("test");
    }

    @DisplayName("Session에 저장된 속성값을 제거한다.")
    @Test
    void removeAttribute() {
        String uuid = UUID.randomUUID().toString();
        HttpSession httpSession = new HttpSession(uuid);

        httpSession.setAttribute("user", "test");

        httpSession.removeAttribute("user");
        assertThat(httpSession.getAttribute("user")).isNull();
    }
}