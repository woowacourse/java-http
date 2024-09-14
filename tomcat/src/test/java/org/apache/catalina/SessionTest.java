package org.apache.catalina;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.apache.catalina.session.Session;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SessionTest {

    @Test
    @DisplayName("세션 ID는 null이 아니다")
    void testSessionIdIsNotNull() {
        Session session = new Session();

        assertThat(session.getId()).isNotNull();
    }

    @Test
    @DisplayName("세션에 속성을 추가하고 가져올 수 있다")
    void testSetAttribute() {
        Session session = new Session();

        session.setAttribute("key", "value");

        assertThat(session.getAttribute("key")).isEqualTo("value");
    }

    @Test
    @DisplayName("세션에 속성을 제거할 수 있다")
    void testRemoveAttribute() {
        Session session = new Session();

        session.setAttribute("key", "value");
        session.removeAttribute("key");

        assertThat(session.getAttribute("key")).isNull();
    }

    @Test
    @DisplayName("세션을 무효화할 수 있다")
    void testInvalidate() {
        Session session = new Session();

        session.setAttribute("key", "value");
        session.invalidate();

        assertThat(session.getAttribute("key")).isNull();
    }

    @Test
    @DisplayName("여러 속성을 추가할 수 있다")
    void testMultipleAttributes() {
        Session session = new Session();

        session.setAttribute("key1", "value1");
        session.setAttribute("key2", "value2");

        assertAll(
                () -> assertThat(session.getAttribute("key1")).isEqualTo("value1"),
                () -> assertThat(session.getAttribute("key2")).isEqualTo("value2")
        );
    }
}
