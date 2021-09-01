package nextstep.jwp.application.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserTest {

    @DisplayName("password 검증 기능")
    @Test
    void checkPasswordTest() {
        //given
        final User user = new User("gugu", "password", "hkkang@woowahan.com");
        //when
        //then
        assertThat(user.checkPassword("password")).isTrue();
        assertThat(user.checkPassword("wordpass")).isFalse();
    }

    @DisplayName("user Id를 set해주는 기능")
    @Test
    void identifiedUserTest() {
        //given
        final User user = new User("gugu", "password", "hkkang@woowahan.com");
        //when
        User identifiedEntity = user.toIdentifiedEntity(1L);
        //then
        assertThat(identifiedEntity.id()).isEqualTo(1L);
    }
}