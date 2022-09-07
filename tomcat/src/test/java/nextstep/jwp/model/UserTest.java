package nextstep.jwp.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import nextstep.jwp.exception.EmptyParameterException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class UserTest {

    @DisplayName("유저 비밀번호가 일치하는지 확인한다.")
    @ParameterizedTest
    @CsvSource(value = {"qwer1234,true", "asdf1234,false"})
    void checkPassword(final String password, final boolean expected) {
        // given
        final User user = new User("sun", "qwer1234", "sun@gmail.com");

        // when
        final boolean actual = user.checkPassword(password);

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("필수 필드가 null이면 예외가 발생한다.")
    @ParameterizedTest
    @CsvSource(value = {
            "sun,email,", "sun,,password", ",email,password"
    })
    void create_throwsException_ifInputIsNull(final String account, final String email, final String password) {
        // given, when, then
        System.out.println("account = " + account);
        System.out.println("email = " + email);
        System.out.println("password = " + password);
        assertThatCode(() -> new User(account, password, email))
                .isInstanceOf(EmptyParameterException.class);
    }

    @DisplayName("필수 필드가 blank면 예외가 발생한다.")
    @Test
    void create_throwsException_ifInputIsBlank() {
        // given, when, then
        assertThatCode(() -> new User("", "", ""))
                .isInstanceOf(EmptyParameterException.class);
    }
}
