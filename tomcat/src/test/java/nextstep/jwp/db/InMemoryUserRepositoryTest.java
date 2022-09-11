package nextstep.jwp.db;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import nextstep.jwp.model.User;
import org.junit.jupiter.api.Test;

class InMemoryUserRepositoryTest {

    @Test
    void 이미_저장된_계정으로_다시_저장할_수_없다() {
        User user = new User("gugu", "1234", "dbswnfl2@gmail.com");
        assertThatThrownBy(() -> InMemoryUserRepository.save(user))
                .isExactlyInstanceOf(IllegalStateException.class)
                .hasMessage("이미 저장된 계정입니다.");
    }

}
