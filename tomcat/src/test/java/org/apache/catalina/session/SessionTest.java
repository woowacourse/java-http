package org.apache.catalina.session;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("세션")
class SessionTest {

    @Test
    @DisplayName("주어진 아이디를 그대로 가진다")
    void keepGivenId() {
        // expect
        assertThat(new Session("abc123").getId()).isEqualTo("abc123");
    }

    @Test
    @DisplayName("생성할 때마다 다른 아이디를 만든다")
    void createWithUniqueId() {
        // when
        final Session first = Session.create();
        final Session second = Session.create();

        // then
        assertThat(first.getId()).isNotBlank();
        assertThat(first.getId()).isNotEqualTo(second.getId());
    }

    @Test
    @DisplayName("속성을 담고 꺼낸다")
    void storeAttribute() {
        // given
        final Session session = Session.create();

        // when
        session.setAttribute("user", "gugu");

        // then
        assertThat(session.getAttribute("user")).isEqualTo("gugu");
    }

    @Test
    @DisplayName("담지 않은 속성은 null을 반환한다")
    void nullWhenAttributeIsAbsent() {
        // expect
        assertThat(Session.create().getAttribute("user")).isNull();
    }

    @Test
    @DisplayName("속성을 지운다")
    void removeAttribute() {
        // given
        final Session session = Session.create();
        session.setAttribute("user", "gugu");

        // when
        session.removeAttribute("user");

        // then
        assertThat(session.getAttribute("user")).isNull();
    }

    @Test
    @DisplayName("null을 담으면 속성을 지운다")
    void removeAttributeWhenValueIsNull() {
        // given
        final Session session = Session.create();
        session.setAttribute("user", "gugu");

        // when
        session.setAttribute("user", null);

        // then
        assertThat(session.getAttribute("user")).isNull();
    }

    @Test
    @DisplayName("담긴 속성 이름을 모두 알려준다")
    void listAttributeNames() {
        // given
        final Session session = Session.create();
        session.setAttribute("user", "gugu");
        session.setAttribute("theme", "dark");

        // when
        final List<String> names = Collections.list(session.getAttributeNames());

        // then
        assertThat(names).containsExactlyInAnyOrder("user", "theme");
    }

    @Test
    @DisplayName("구현하지 않은 기능은 예외를 던진다")
    void throwOnUnsupportedOperation() {
        // given
        final Session session = Session.create();

        // expect
        assertThatThrownBy(session::invalidate).isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(session::getCreationTime).isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(session::getLastAccessedTime).isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(session::getServletContext).isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(session::getMaxInactiveInterval).isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> session.setMaxInactiveInterval(60)).isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(session::isNew).isInstanceOf(UnsupportedOperationException.class);
    }
}
