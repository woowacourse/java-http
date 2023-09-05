package org.apache.coyote.http11.common;

import static org.assertj.core.api.Assertions.assertThat;

import nextstep.jwp.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayName("SessionManager 단위 테스트")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class SessionManagerTest {

    @Test
    void 새로운_세션_등록() {
        // given
        final Session session = new Session("randomUUID");
        session.setAttribute("user", new User("royce", "password", "email"));

        // when
        SessionManager.add(session);

        // then
        final Session findSession = SessionManager.findSession(session.getId());
        assertThat(findSession).isNotNull();
        assertThat(findSession.getId()).isEqualTo(session.getId());
    }

    @Test
    void 등록된_세션_조회() {
        // given
        final Session session = new Session("randomUUID");
        session.setAttribute("user", new User("royce", "password", "email"));
        SessionManager.add(session);

        // when
        final Session findSession = SessionManager.findSession(session.getId());

        // then
        assertThat(findSession).isNotNull();
        assertThat(findSession.getId()).isEqualTo(session.getId());
    }

    @Test
    void 세션_삭제() {
        // given
        final Session session = new Session("randomUUID");
        session.setAttribute("user", new User("royce", "password", "email"));
        SessionManager.add(session);

        // when
        SessionManager.remove(session.getId());

        // then
        final Session findSession = SessionManager.findSession(session.getId());
        assertThat(findSession).isNull();
    }
}
