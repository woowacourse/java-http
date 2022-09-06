package org.apache.coyote.http11.session;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import nextstep.jwp.domain.model.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SessionManagerTest {

    @BeforeEach
    void setUp() {
        SessionManager.getInstance().clear();
    }

    @Test
    @DisplayName("세션에 유저정보를 저장하고 찾는다.")
    void saveAndFind() {
        final SessionManager sessionManager = SessionManager.getInstance();
        final long id = 1L;
        final String account = "alex";
        final String password = "password";
        final String email = "alex@email.com";

        final String jSessionId = sessionManager.addSession(new User(id, account, password, email));

        final User findUser = sessionManager.findSession(jSessionId)
                .get();

        assertAll(
                () -> assertThat(findUser.getId()).isEqualTo(id),
                () -> assertThat(findUser.getAccount()).isEqualTo(account),
                () -> assertThat(findUser.getPassword()).isEqualTo(password),
                () -> assertThat(findUser.getEmail()).isEqualTo(email)
        );
    }
}
