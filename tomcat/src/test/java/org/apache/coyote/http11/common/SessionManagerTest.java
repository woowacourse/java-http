package org.apache.coyote.http11.common;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class SessionManagerTest {

    @Test
    void 세션을_추가한다() throws IOException {
        UUID uuid = UUID.randomUUID();
        Session session = new Session(uuid);
        SessionManager.add(session);

        assertThat(SessionManager.findSession(uuid)).isEqualTo(session);
    }
}
