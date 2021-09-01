package nextstep.jwp.http.session;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class HttpSessionsTest {

    @DisplayName("session을 얻어온다.")
    @Test
    void getSession() {
        String uuid = UUID.randomUUID().toString();
        HttpSession httpSession = HttpSessions.getSession(uuid);

        assertThat(httpSession.getId()).isEqualTo(uuid);
    }
}