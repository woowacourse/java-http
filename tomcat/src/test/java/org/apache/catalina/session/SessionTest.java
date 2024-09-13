package org.apache.catalina.session;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SessionTest {

    @DisplayName("세션 ID를 반환한다.")
    @Test
    void getIdTest() {
        // given
        String id = "testId";
        Session session = new Session(id);

        // when
        String result = session.getId();

        // then
        assertThat(result).isEqualTo(id);
    }

    @DisplayName("세션 속성을 반환한다.")
    @Test
    void getAttributeTest() {
        // given
        String id = "testId";
        Session session = new Session(id);
        session.setAttribute("username", "mangcho");

        // when
        Object result = session.getAttribute("username");

        // then
        assertThat(result).isEqualTo("mangcho");
    }

    @DisplayName("세션 속성을 설정한다.")
    @Test
    void setAttribute() {
        // given
        String id = "testId";
        Session session = new Session(id);

        // when
        session.setAttribute("username", "mangcho");

        // then
        Object username = session.getAttribute("username");
        assertThat(username).isEqualTo("mangcho");
    }
}
