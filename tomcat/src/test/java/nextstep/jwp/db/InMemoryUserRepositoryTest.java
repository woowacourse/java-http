package nextstep.jwp.db;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import nextstep.jwp.model.User;
import org.junit.jupiter.api.Test;

class InMemoryUserRepositoryTest {

    @Test
    void save() {
        User huni = new User("huni", "1234", "huni@huni.com");
        User jaehun = new User("jaehun", "1234", "jeahun@jaehun.com");

        InMemoryUserRepository.save(huni);
        InMemoryUserRepository.save(jaehun);

        assertAll(
                () -> assertThat(InMemoryUserRepository.findByAccount("huni").get().getId()).isEqualTo(2L),
                () -> assertThat(InMemoryUserRepository.findByAccount("jaehun").get().getId()).isEqualTo(3L)
        );
    }

}
