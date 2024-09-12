package org.apache.catalina.session;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

class SessionTest {

    @Test
    void Session_객체를_생성하면_manager에_저장한다() {
        // given
        String id = "1";
        SessionManager manager = SessionManager.getInstance();

        // when
        Session session = new Session(id, manager);

        // then
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(session.getId()).isEqualTo(id);
            softly.assertThat(manager.findSession(id)).isEqualTo(session);
        });
    }

    @Test
    void attribute를_추가한다() {
        // given
        Session session = new Session("1", SessionManager.getInstance());

        // when
        session.setAttribute("name", "prin");

        // then
        assertThat(session.getAttribute("name")).isEqualTo("prin");
    }

    @Test
    void attribute의_name이_비어있으면_추가할_때_예외가_발생한다() {
        // given
        Session session = new Session("1", SessionManager.getInstance());

        // when & then
        assertThatThrownBy(() -> session.setAttribute(null, "prin"))
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Attribute의 이름은 필수입니다.");
    }

    @Test
    void 만료되지_않은_유효한_세션이면_true를_반환한다() {
        // given
        Session session = new Session("1", SessionManager.getInstance());

        long now = System.currentTimeMillis();
        session.setCreateTime(now - 1799999);

        // when
        boolean isValid = session.isValid();

        // then
        assertThat(isValid).isTrue();
    }

    @Test
    void 만료된_유효하지_않은_세션이면_manager에서_삭제하고_false를_반환한다() {
        // given
        String id = "1";
        SessionManager manager = SessionManager.getInstance();
        Session session = new Session(id, manager);

        long now = System.currentTimeMillis();
        session.setCreateTime(now - 1800000);

        // when
        boolean isValid = session.isValid();

        // then
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(isValid).isFalse();
            softly.assertThat(manager.findSession(id)).isNull();
        });
    }
}
