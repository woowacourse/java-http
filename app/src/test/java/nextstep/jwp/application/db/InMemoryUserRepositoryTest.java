package nextstep.jwp.application.db;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.stream.IntStream;
import nextstep.jwp.application.domain.Account;
import nextstep.jwp.application.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class InMemoryUserRepositoryTest {

    @DisplayName("인메모리 DB 저장 테스트")
    @Test
    void saveTest() {
        //given
        final User user = new User("wedge", "password", "hkkang@woowahan.com");
        //when
        InMemoryUserRepository.save(user);
        //then
        User wedge = InMemoryUserRepository.findByAccount("wedge").get();
        assertThat(wedge.account()).isEqualTo(new Account("wedge"));
        assertThat(wedge.checkPassword("password")).isTrue();
    }

    @DisplayName("중복이면 예외가 발생한다.")
    @Test
    void duplicateCheck() {
        //given
        final User user1 = new User("duple", "password", "hkkang@woowahan.com");
        final User user2 = new User("duple", "password", "hkkang@woowahan.com");

        //when
        InMemoryUserRepository.save(user1);
        //then
        assertThatThrownBy(() -> InMemoryUserRepository.save(user2))
            .hasMessageContaining("존재하는 계정명 입니다.");
    }

    @DisplayName("유저 ID 자동 생성 테스트")
    @Test
    void autoIncrementTest() {
        //given
        List<User> users = IntStream.rangeClosed(0, 10)
            .mapToObj(i -> new User("wedge" + i, "password", "kk@naver.com"))
            .collect(toList());
        //when
        users.forEach(InMemoryUserRepository::save);
        //then
        User wedge0 = InMemoryUserRepository.findByAccount("wedge0").get();
        User wedge9 = InMemoryUserRepository.findByAccount("wedge9").get();

        assertThat(wedge0.id() + 9).isEqualTo(wedge9.id());
    }
}