package org.apache.coyote.http11.common;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SessionManagerTest {

    @Test
    void 세션을_추가한다() throws IOException {
        UUID uuid = UUID.randomUUID();
        Session session = new Session(uuid);
        SessionManager.add(session);

        assertThat(SessionManager.findSession(uuid)).isEqualTo(session);
    }

    @Test
    void 존재하지_않는_세션_조회_시_예외() {
        assertThatThrownBy(() -> SessionManager.findSession(UUID.randomUUID()))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
