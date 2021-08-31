package nextstep.jwp.http;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpSessionsTest {
    @Test
    @DisplayName("Session을 추가한다.")
    void put() {
        String id = "id";
        HttpSession session = new HttpSession(id);
        HttpSessions.put(session);
        assertThat(HttpSessions.getSession(id)).isSameAs(session);
    }

    @Test
    @DisplayName("Session을 제거한다.")
    void remove() {
        String id = "id";
        HttpSession session = new HttpSession(id);
        HttpSessions.put(session);
        HttpSessions.remove(id);
        assertThat(HttpSessions.getSession(id)).isNull();
    }
}