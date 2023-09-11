package org.apache.coyote.http11.common;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class SessionTest {

    @Test
    void 값을_설정한다() {
        // given
        final Session session = new Session("UUID");

        // when
        session.setAttribute("hello", "world");

        // then
        assertThat(session.getAttribute("hello")).isEqualTo("world");
    }

    @Test
    void 값을_가져온다() {
        // given
        final Session session = new Session("UUID");
        session.setAttribute("hello", "world");

        // expect
        assertThat(session.getAttribute("hello")).isEqualTo("world");
    }

    @Test
    void 값을_제거한다() {
        // given
        final Session session = new Session("UUID");
        session.setAttribute("hello", "world");

        // when
        session.removeAttribute("hello");

        // then
        assertThat(session.getAttribute("hello")).isNull();
    }

    @Test
    void 세션에_있는_모든_값을_제거한다() {
        // given
        final Session session = new Session("UUID");
        session.setAttribute("hello", "world");
        session.setAttribute("hello2", "world2");

        // when
        session.invalidate();

        // then
        assertThat(session.getItems()).isEmpty();
    }
}
