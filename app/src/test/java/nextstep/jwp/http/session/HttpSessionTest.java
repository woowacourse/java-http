package nextstep.jwp.http.session;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class HttpSessionTest {

    @Test
    void getId() {
        String uuid = UUID.randomUUID().toString();
        HttpSession httpSession = new HttpSession(uuid);
        assertThat(httpSession.getId()).isEqualTo(uuid);
    }

    @Test
    void setAttribute() {
        String uuid = UUID.randomUUID().toString();
        HttpSession httpSession = new HttpSession(uuid);

        httpSession.setAttribute("user", "test");
        assertThat(httpSession.getAttribute("user")).isEqualTo("test");
    }

    @Test
    void removeAttribute() {
        String uuid = UUID.randomUUID().toString();
        HttpSession httpSession = new HttpSession(uuid);

        httpSession.setAttribute("user", "test");

        httpSession.removeAttribute("user");
        assertThat(httpSession.getAttribute("user")).isNull();
    }
}