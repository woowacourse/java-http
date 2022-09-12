package application.jwp.infra;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import nextstep.jwp.domain.User;
import nextstep.jwp.infra.InMemoryUserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class InMemoryUserRepositoryTest {

    final InMemoryUserRepository userRepository = InMemoryUserRepository.getInstance();

    @Test
    @DisplayName("회원을 가입시킨다.")
    void save() {
        final User user = new User("test", "testPassword", "test@test.com");

        final User actual = userRepository.save(user);

        User expected = userRepository.findByAccount(actual.getAccount()).get();

        assertAll(
                () -> assertThat(actual.getEmail()).isEqualTo(expected.getEmail()),
                () -> assertThat(actual.getPassword()).isEqualTo(expected.getPassword()),
                () -> assertThat(actual.getAccount()).isEqualTo(expected.getAccount())
        );
    }

    @Test
    @DisplayName("유저 저장시 동시성 테스트를 진행한다.")
    void save_concurrency() throws InterruptedException {
        final ExecutorService executorService = Executors.newFixedThreadPool(1000);
        CountDownLatch countDownLatch = new CountDownLatch(1000);
        List<User> users = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            users.add(new User("test"+ i , "password","test1@test.com"));
        }

        for (User testUser :users) {
            executorService.execute(() -> {
                userRepository.save(testUser);
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();
        final Long test0Id = userRepository.findByAccount("test0").get().getId();
        final Long test999Id = userRepository.findByAccount("test999").get().getId();
        assertThat(test999Id - test0Id).isEqualTo(999L);
    }
}
