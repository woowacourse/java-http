package org.apache.catalina.session;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.techcourse.model.User;
import org.junit.jupiter.api.Test;

public class SessionTest {

    @Test
    void 세션이_랜덤_ID로_생성된다() {
        // when
        Session session = new Session();

        // then
        assertAll(
                () -> assertThat(session.getId()).isNotNull(),
                () -> assertThat(session.getId()).hasSize(36)
        );
    }

    @Test
    void 세션이_주어진_ID로_생성된다() {
        // given
        String sessionId = "customId";

        // when
        Session session = new Session(sessionId);

        // then
        assertThat(session.getId()).isEqualTo(sessionId);
    }

    @Test
    void 세션에_속성을_추가할_수_있다() {
        // given
        Session session = new Session();
        String attributeName = "user";
        User attributeValue = new User("dora", "password", "email");

        // when
        session.setAttribute(attributeName, attributeValue);

        // then
        assertThat(session.getAttribute(attributeName)).isEqualTo(attributeValue);
    }
}
