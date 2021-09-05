package nextstep.joanne.dashboard.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;

class RegisterServiceTest {

    @DisplayName("회원 가입을 한다.")
    @Test
    void join() {
        // given
        String account = "joanne";
        String email = "joanne@woowahan.com";
        String password = "1234";

        // when
        RegisterService registerService = new RegisterService();
        assertThatCode(() -> registerService.join(account, email, password))
                .doesNotThrowAnyException();
    }
}