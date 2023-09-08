package nextstep.jwp.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("NonAsciiCharacters")
class UserTest {

    @Test
    void 유저의_비밀번호가_일치하면_true를_반환한다() {
        // given
        final String password = "bebe";
        final User user = new User("bebe account", password, "bebe@wooteco.com");

        // when, then
        assertThat(user.checkPassword(password)).isTrue();
    }

    @Test
    void 유저의_비밀번호가_일치하지_않으면_false를_반환한다() {
        // given
        final String password = "bebe";
        final User user = new User("bebe account", password, "bebe@wooteco.com");

        // when
        final String wrongPassword = "ditoo";

        // then
        assertThat(user.checkPassword(wrongPassword)).isFalse();
    }
}
