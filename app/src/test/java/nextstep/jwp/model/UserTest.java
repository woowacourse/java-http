package nextstep.jwp.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserTest {

    @DisplayName("필수 값이 null 일 경우 예외가 발생한다.")
    @Test
    void createException() {
        assertThatThrownBy(() ->
                new User("account", null, null)
        ).isInstanceOf(IllegalArgumentException.class);
    }
}
