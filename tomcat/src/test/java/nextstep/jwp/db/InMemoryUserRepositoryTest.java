package nextstep.jwp.db;

import static org.assertj.core.api.Assertions.assertThat;

import nextstep.jwp.model.User;
import org.junit.jupiter.api.Test;

class InMemoryUserRepositoryTest {

    @Test
    void 저장할_때_마다_순차적으로_아이디가_증가한다() {
        InMemoryUserRepository.save(new User("test1", "test", "email@eamil"));
        InMemoryUserRepository.save(new User("test2", "test", "email@eamil"));
        InMemoryUserRepository.save(new User("test3", "test", "email@eamil"));

        Long id2 = InMemoryUserRepository.findByAccount("test2")
                                         .map(User::getId)
                                         .orElse(0L);

        Long id3 = InMemoryUserRepository.findByAccount("test3")
                                         .map(User::getId)
                                         .orElse(0L);

        assertThat(id3 - id2).isOne();
    }

}
