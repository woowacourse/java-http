package org.apache.coyote.http11.session;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
class SessionTest {

    @Test
    void 정상적으로_세션을_생성할_수_있다() {
        // given when
        final Session session = new Session();

        // then
        assertThat(session).isNotNull();
    }

    @Test
    void 세션에_값을_등록할_수_있다() {
        // given when
        final Session session = new Session();

        // then
        assertDoesNotThrow(
                () -> session.setAttribute("name", "wuga")
        );
    }

    @Test
    void 세션에_등록된_값을_조회할_수_있다() {
        // given
        final Session session = new Session();
        session.setAttribute("name", "wuga");

        // when
        final var actual = (String) session.getAttribute("name");

        // then
        assertThat(actual).isEqualTo("wuga");
    }

    @Test
    void 세션에_등록된_값을_삭제할_수_있다() {
        // given
        final Session session = new Session();
        session.setAttribute("name", "wuga");

        // when
        session.removeAttribute("name");
        final var actual = session.getAttribute("name");

        // then
        assertThat(actual).isNull();
    }

    @Test
    void 세션을_초기화할_수_있다() {
        // given
        final Session session = new Session();
        session.setAttribute("name", "wuga");

        // when
        session.invalidate();
        final var actual = session.getAttribute("name");

        // then
        assertThat(actual).isNull();
    }

    @Test
    void 세션_아이디를_반환한다() {
        // given
        final Session session = new Session();

        // when
        final var actual = session.getId();

        // then
        assertThat(actual).isNotNull();
    }
}
