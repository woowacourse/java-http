package jwp.model;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.jwp.model.User;

public class UserTest {

    @Test
    @DisplayName("비밀번호가 맞는지 확인한다.")
    void checkPassword() {
        // given
        User user = new User("panda", "password", "panda@gmail.com");

        // when, then
        assertAll(
            () -> assertThat(user.checkPassword("password")).isTrue(),
            () -> assertThat(user.checkPassword("wrong")).isFalse()
        );
    }
}
