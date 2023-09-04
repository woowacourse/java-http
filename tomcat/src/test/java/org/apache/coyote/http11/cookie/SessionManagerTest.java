package org.apache.coyote.http11.cookie;

import org.apache.coyote.http11.exception.NotFoundSessionException;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import static org.apache.coyote.http11.cookie.SessionManager.findSession;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class SessionManagerTest {

    @Test
    void 세션을_추가한다() {
        // given
        Session session = Session.uuid();

        // when
        SessionManager.add(session);

        // then
        Session findSession = findSession(session.getId());
        assertThat(findSession).isEqualTo(session);
    }

    @Test
    void 세션을_제거한다() {
        // given
        Session session = Session.uuid();
        SessionManager.add(session);
        String sessionId = session.getId();

        // when
        SessionManager.remove(sessionId);

        // then
        assertThatThrownBy(() -> SessionManager.findSession(sessionId))
                .isInstanceOf(NotFoundSessionException.class);
    }
}
