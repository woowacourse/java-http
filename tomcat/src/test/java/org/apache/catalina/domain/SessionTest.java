package org.apache.catalina.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SessionTest {

    @DisplayName("세션 생성 시 ID가 설정된다")
    @Test
    void createSession_withId() {
        // given
        String sessionId = "test-session-id";

        // when
        Session session = new Session(sessionId);

        // then
        assertThat(session.getId()).isEqualTo(sessionId);
    }

    @DisplayName("setAttribute와 getAttribute가 정상 작동한다")
    @Test
    void setAttribute_getAttribute() {
        // given
        Session session = new Session("test-id");
        String key = "username";
        String value = "testUser";

        // when
        session.setAttribute(key, value);

        // then
        assertThat(session.getAttribute(key)).isEqualTo(value);
    }

    @DisplayName("removeAttribute가 정상 작동한다")
    @Test
    void removeAttribute() {
        // given
        Session session = new Session("test-id");
        String key = "username";
        String value = "testUser";
        session.setAttribute(key, value);

        // when
        session.removeAttribute(key);

        // then
        assertThat(session.getAttribute(key)).isNull();
    }

    @DisplayName("invalidate가 모든 속성을 제거한다")
    @Test
    void invalidate_clearsAllAttributes() {
        // given
        Session session = new Session("test-id");
        session.setAttribute("key1", "value1");
        session.setAttribute("key2", "value2");

        // when
        session.invalidate();

        // then
        assertThat(session.getAttribute("key1")).isNull();
        assertThat(session.getAttribute("key2")).isNull();
    }

    @DisplayName("여러 속성을 저장하고 조회할 수 있다")
    @Test
    void multipleAttributes() {
        // given
        Session session = new Session("test-id");

        // when
        session.setAttribute("username", "admin");
        session.setAttribute("loginTime", System.currentTimeMillis());
        session.setAttribute("role", "ADMIN");

        // then
        assertThat(session.getAttribute("username")).isEqualTo("admin");
        assertThat(session.getAttribute("loginTime")).isNotNull();
        assertThat(session.getAttribute("role")).isEqualTo("ADMIN");
    }
}