package org.apache.coyote.http11.session;

import static org.assertj.core.api.Assertions.assertThat;

import nextstep.jwp.model.User;
import org.junit.jupiter.api.Test;

class SessionTest {

    @Test
    void 세션에_유저_정보를_저장하고_조회할_수_있다() {
        // given
        Session session = new Session("jsessionid");

        // when
        User user = new User(null, "account", "password", "email");
        session.setUserAttribute(user);

        // then
        assertThat(session.getUserAttribute()).isEqualTo(user);
    }
}
