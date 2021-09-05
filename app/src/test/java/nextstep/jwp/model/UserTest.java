package nextstep.jwp.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class UserTest {

    private User user;
    @BeforeEach
    private void setUp() {
        this.user = new User(1L, "test", "password", "test@test.com");
    }

    @DisplayName("비밀번호 입력을 체크한다.")
    @Test
    void checkPassword() {
        assertThat(user.checkPassword("password")).isTrue();
        assertThat(user.checkPassword("passwordFail")).isFalse();
    }

    @DisplayName("User id를 리턴한다.")
    @Test
    void getId() {
        assertThat(user.getId()).isEqualTo(1L);
    }

    @DisplayName("User account를 리턴한다.")
    @Test
    void getAccount() {
        assertThat(user.getAccount()).isEqualTo("test");
    }

    @DisplayName("User password를 리턴한다.")
    @Test
    void getPassword() {
        assertThat(user.getPassword()).isEqualTo("password");
    }

    @DisplayName("User email를 리턴한다.")
    @Test
    void getEmail() {
        assertThat(user.getEmail()).isEqualTo("test@test.com");
    }
}