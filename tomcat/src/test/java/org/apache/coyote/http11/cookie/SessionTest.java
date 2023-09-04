package org.apache.coyote.http11.cookie;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class SessionTest {

    @Test
    void uuid로_새로운_세션을_생성한다() {
        // given
        Session session = Session.uuid();

        // when
        String sessionId = session.getId();

        // then
        assertThat(sessionId).isNotNull();
    }

    @Test
    void 세션에_정보를_저장한다() {
        // given
        Session session = Session.uuid();

        // when
        session.setAttribute("name", "boxster");

        // then
        assertThat(session.getAttribute("name")).isEqualTo("boxster");
    }

    @Test
    void 세션의_정보를_삭제한다() {
        // given
        Session session = Session.uuid();
        session.setAttribute("name", "boxster");

        // when
        session.removeAttribute("name");

        // then
        assertThat(session.getAttribute("name")).isNull();
    }

    @Test
    void 세션의_정보를_모두_삭제한다() {
        // given
        Session session = Session.uuid();
        session.setAttribute("name", "boxster");
        session.setAttribute("age", "20");

        // when
        session.invalidate();

        // then
        assertThat(session.getAttribute("name")).isNull();
        assertThat(session.getAttribute("age")).isNull();
    }
}
