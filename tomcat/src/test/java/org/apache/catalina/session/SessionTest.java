package org.apache.catalina.session;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

class SessionTest {

    @Test
    void Session_객체를_생성한다() {
        // given
        String id = "1";

        // when
        Session session = new Session(id, System.currentTimeMillis());

        // then
        assertThat(session.getId()).isEqualTo(id);
    }

    @Test
    void attribute를_추가한다() {
        // given
        Session session = new Session("1", System.currentTimeMillis());

        // when
        session.setAttribute("name", "prin");

        // then
        assertThat(session.getAttribute("name")).isEqualTo("prin");
    }

    @Test
    void attribute의_name이_비어있으면_추가할_때_예외가_발생한다() {
        // given
        Session session = new Session("1", System.currentTimeMillis());

        // when & then
        assertThatThrownBy(() -> session.setAttribute(null, "prin"))
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Attribute의 이름은 필수입니다.");
    }

    @Test
    void 만료되지_않은_유효한_세션이면_true를_반환한다() {
        // given
        long createdTime = System.currentTimeMillis() - 1799999;
        Session session = new Session("1", createdTime);

        // when
        boolean isValid = session.isValid();

        // then
        assertThat(isValid).isTrue();
    }

    @Test
    void 만료된_유효하지_않은_세션이면_false를_반환한다() {
        // given
        long createdTime = System.currentTimeMillis() - 1800000;
        Session session = new Session("1", createdTime);

        // when
        boolean isValid = session.isValid();

        // then
        assertThat(isValid).isFalse();
    }
}
