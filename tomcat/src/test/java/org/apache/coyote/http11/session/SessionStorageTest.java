package org.apache.coyote.http11.session;

import static org.assertj.core.api.Assertions.assertThat;

import nextstep.jwp.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SessionStorageTest {

    @DisplayName("세션 ID로 account를 찾을 수 있다.")
    @Test
    void getAccountBySessionId() {
        // given
        User user = new User("devhudi", "password", "devhudi@gmail.com");
        String sessionId = SessionStorage.add(user);

        // when
        User actual = SessionStorage.findAccountBySessionId(sessionId).get();

        // then
        assertThat(actual.getAccount()).isEqualTo("devhudi");
    }

}
