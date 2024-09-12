package org.apache.catalina.session;

import static org.assertj.core.api.Assertions.assertThat;

import com.techcourse.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SessionTest {

    @DisplayName("객체를 저장할 수 있다.")
    @Test
    void store() {
        // given
        Session session = new Session();
        User user = new User("poke", "password", "poke@zzang.com");

        // when
        session.store("user", user);
        User storedUser = (User) session.findValue("user");

        // then
        assertThat(user).isEqualTo(storedUser);
    }
}