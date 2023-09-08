package org.apache.catalina;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class SessionManagerTest {

    private final SessionManager sessionManager = new SessionManager();

    @Test
    void 세션을_추가한다() {
        // given
        final Session session = new Session("hello");

        // when
        sessionManager.add(session);

        // then
        assertThat(sessionManager.findSession("hello")).isEqualTo(session);
    }

    @Test
    void id를_입력받아_세션을_반환한다() {
        // given
        final Session session = new Session("world");
        sessionManager.add(session);

        // when
        final Session findSession = sessionManager.findSession("world");

        // then
        assertThat(findSession).isEqualTo(session);
    }

    @Test
    void 세션을_제거한다() {
        // given
        final Session session = new Session("helloworld");
        sessionManager.add(session);

        // when
        sessionManager.remove("helloworld");

        // then
        assertThat(sessionManager.findSession("helloworld")).isNull();
    }
}
