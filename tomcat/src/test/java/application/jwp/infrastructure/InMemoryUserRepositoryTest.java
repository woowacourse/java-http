package application.jwp.infrastructure;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import nextstep.jwp.domain.model.User;
import nextstep.jwp.domain.model.UserRepository;
import nextstep.jwp.infrastructure.InMemoryUserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class InMemoryUserRepositoryTest {

    @Test
    @DisplayName("멀티 스레드 환경에서 동시저장")
    void multiSave() throws InterruptedException {
        final UserRepository userRepository = InMemoryUserRepository.getInstance();

        final ExecutorService executorService = Executors.newFixedThreadPool(1000);
        final CountDownLatch countDownLatch = new CountDownLatch(1000);
        final User firstSaved = userRepository.save(new User("firstTest", "password", "email@email.com"));
        final List<User> testUsers = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            testUsers.add(new User("test" + i, "password", "email" + i + "@email.com"));
        }

        for (User testUser : testUsers) {
            executorService.submit(() -> {
                userRepository.save(testUser);
                countDownLatch.countDown();
            });
        }

        countDownLatch.await();
        final List<User> all = userRepository.findAll();
        final long maxId = all.stream()
                .mapToLong(User::getId)
                .max()
                .getAsLong();

        assertThat(maxId - firstSaved.getId()).isEqualTo(1000L);
    }
}