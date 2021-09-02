package nextstep.jwp.db;

import nextstep.jwp.exception.MemberNotFoundException;
import nextstep.jwp.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryUserRepositoryTest {

    @DisplayName("유저를 DB에 저장한다.")
    @Test
    void save() {
        // given
        String account = "charlie";
        String password = "1234";
        String email = "test@test.com";

        // when
        InMemoryUserRepository.save(new User(account, password, email));

        // then
        Optional<User> optional = InMemoryUserRepository.findByAccount(account);
        assertThat(optional).isNotEmpty();
    }

    @DisplayName("같은 닉네임의 유저가 DB에 이미 존재하는지 확인한다.")
    @Test
    void existsUser() {
        // given
        String account = "charlie";
        String password = "1234";
        String email = "test@test.com";
        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);

        // when
        boolean result = InMemoryUserRepository.existsUser(new User(account, password, email));

        // then
        assertThat(result).isTrue();

        // when
        boolean result2 = InMemoryUserRepository.existsUser(new User(account + "1", password, email));

        // then
        assertThat(result2).isFalse();
    }

    @DisplayName("계정 이름으로 유저를 조회한다.")
    @Test
    void findByAccount() {
        // given
        String account = "charlie";
        String password = "1234";
        String email = "test@test.com";
        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);

        // when
        User findUser = InMemoryUserRepository.findByAccount(account).orElseThrow(MemberNotFoundException::new);

        // then
        assertThat(findUser.getAccount()).isEqualTo(account);
        assertThat(findUser.getPassword()).isEqualTo(password);
        assertThat(findUser.getEmail()).isEqualTo(email);
    }
}