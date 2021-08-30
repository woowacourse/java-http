package nextstep.joanne.db;

import nextstep.joanne.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class InMemoryUserRepositoryTest {

    @Test
    @DisplayName("User를 저장한다.")
    void save() {
        User user = new User("joanne", "password", "seominjeong.dev@gmail.com");
        assertDoesNotThrow(() -> InMemoryUserRepository.save(user));
    }

    @Test
    @DisplayName("account로 등록된 User를 조회한다.")
    void findByAccount() {
        Optional<User> user = InMemoryUserRepository.findByAccount("gugu");
        assertThat(user).isPresent();
    }

    @Test
    @DisplayName("등록된 유저의 다음 순서 Id를 가져온다.")
    void nextId() {
        // given
        User first = new User("joanne", "password", "seominjeong.dev@gmail.com");
        InMemoryUserRepository.save(first);
        long 멍토_앞사람 = InMemoryUserRepository.findByAccount("joanne").get().getId();

        // when
        User second = new User("멍토", "password", "멍토@gmail.com");
        InMemoryUserRepository.save(second);

        assertAll(
                () -> assertThat(InMemoryUserRepository.findByAccount("멍토")).isPresent(),
                () -> assertThat(InMemoryUserRepository.findByAccount("멍토").get().getId()).isEqualTo(멍토_앞사람 + 1)
        );

    }
}