package org.apache.common;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class SessionManagerTest {

    @Test
    void 애플리케이션에_존재하는_특정_세션을_조회한다() {
        String sessionId = UUID.randomUUID().toString();
        Session session = new Session(sessionId);
        SessionManager.add(session);

        Session findSession = SessionManager.findSession(sessionId);

        assertThat(session.getId()).isEqualTo(findSession.getId());
    }

    @Test
    void 애플리케이션에_존재하는_특정_세션을_삭제한다() {
        String sessionId = UUID.randomUUID().toString();
        Session session = new Session(sessionId);
        SessionManager.add(session);

        SessionManager.remove(sessionId);

        assertThat(SessionManager.findSession(session.getId())).isNull();
    }
}
