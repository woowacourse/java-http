package nextstep.jwp.http.session;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class HttpSessionsTest {

    @Test
    void getSession() {
        String uuid = UUID.randomUUID().toString();
        HttpSession httpSession = HttpSessions.getSession(uuid);

        assertThat(httpSession.getId()).isEqualTo(uuid);
    }
}