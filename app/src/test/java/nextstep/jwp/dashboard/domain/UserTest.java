package nextstep.jwp.dashboard.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.jwp.httpserver.exception.AuthorizationException;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("User 도메인 단위 테스트")
class UserTest {

    @Test
    @DisplayName("올바른 비밀번호를 입력한 경우 아무 예외도 발생하지 않는다.")
    void rightPassword() {
        // given
        User user = new User(1L, "air", "1234", "air.junseo@gmail.com");

        // when
        // then
        assertThatNoException().isThrownBy(() -> user.checkPassword("1234"));
    }

    @Test
    @DisplayName("잘못된 비밀번호를 입력한 경우 401 예외가 발생한다.")
    void wrongPassword() {
        // given
        User user = new User(1L, "air", "1234", "air.junseo@gmail.com");

        // when
        // then
        assertThatThrownBy(() -> user.checkPassword("1211"))
                .isInstanceOf(AuthorizationException.class);
    }
}
