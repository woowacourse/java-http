package org.apache.coyote.http11.cookie;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

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
        Session findSession = SessionManager.findSession(session.getId());
        assertThat(findSession).isEqualTo(session);
    }

    @Test
    void 세션을_제거한다() {
        // given
        Session session = Session.uuid();
        SessionManager.add(session);

        // when
        SessionManager.remove(session.getId());

        // then
        Session findSession = SessionManager.findSession(session.getId());
        assertThat(findSession).isNull();
    }
}
