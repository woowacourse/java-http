package nextstep.jwp.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import nextstep.jwp.exception.UnauthorizedException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserTest {

    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String EMAIL = "email";

    @DisplayName("ID와 함께 유저 생성")
    @Test
    void createWithId() {
        // given
        Long id = 1L;
        String expectToString = "User{" +
            "id=" + id +
            ", account='" + ACCOUNT + '\'' +
            ", email='" + EMAIL + '\'' +
            ", password='" + PASSWORD + '\'' +
            '}';

        // when
        User user = new User(ACCOUNT, PASSWORD, EMAIL);
        User userWithId = User.withId(1L, user);

        // then
        assertThat(userWithId.toString()).isEqualTo(expectToString);
    }

    @DisplayName("password 검증 실패시 예외처리")
    @Test
    void checkPasswordException() {
        // given
        User user = new User(ACCOUNT, EMAIL, PASSWORD);

        // when, then
        assertThatThrownBy(() -> user.checkPassword("WRONG_PASSWORD")).isExactlyInstanceOf(
            UnauthorizedException.class);
    }
}