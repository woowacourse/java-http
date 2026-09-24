package org.apache.catalina;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class SessionManagerTest {

    @Test
    void 등록한_세션을_다른_매니저에서도_ID로_찾는다() {
        String id = UUID.randomUUID().toString();
        Session session = new Session(id);

        new SessionManager().add(session);

        assertThat(new SessionManager().findSession(id)).contains(session);
    }
}
