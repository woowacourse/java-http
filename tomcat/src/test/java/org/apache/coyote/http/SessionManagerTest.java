package org.apache.coyote.http;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class SessionManagerTest {

    @Test
    void add_메서드는_HttpSession을_전달하면_해당_세션을_저장한다() {
        final SessionManager sessionManager = new SessionManager();
        final HttpSession session = new HttpSession();

        SoftAssertions.assertSoftly(softAssertions -> {
            assertDoesNotThrow(() -> sessionManager.add(session));
            softAssertions.assertThat(sessionManager.findSession(session.getId())).isEqualTo(session);
        });
    }

    @Test
    void findSession_메서드는_id를_전달하면_HttpSession을_반환한다() {
        final SessionManager sessionManager = new SessionManager();
        final HttpSession session = new HttpSession();
        sessionManager.add(session);

        final HttpSession actual = sessionManager.findSession(session.getId());

        assertThat(actual).isEqualTo(session);
    }

    @Test
    void remove_메서드는_HttpSession을_전달하면_해당_세션을_제거한다() {
        final SessionManager sessionManager = new SessionManager();
        final HttpSession session = new HttpSession();
        sessionManager.add(session);

        SoftAssertions.assertSoftly(softAssertions -> {
            assertDoesNotThrow(() -> sessionManager.remove(session));
            softAssertions.assertThat(sessionManager.findSession(session.getId())).isNull();
        });
    }
}
