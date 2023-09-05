package org.apache.coyote.common;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class SessionManagerTest {

    @Test
    void 세션_매니저에_세션을_추가한다() {
        final Session session = new Session(UUID.randomUUID().toString());

        SessionManager.add(session);

        assertThat(SessionManager.findSession(session.getId())).isEqualTo(session);
    }

    @Test
    void 세션_매니저에서_세션을_삭제한다() {
        final Session session = new Session(UUID.randomUUID().toString());
        SessionManager.add(session);

        SessionManager.remove(session.getId());

        assertThat(SessionManager.findSession(session.getId())).isNull();
    }
}
