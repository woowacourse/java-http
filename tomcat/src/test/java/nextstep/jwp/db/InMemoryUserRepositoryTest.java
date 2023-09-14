package nextstep.jwp.db;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import nextstep.jwp.model.User;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class InMemoryUserRepositoryTest {

    private static final Logger logger = LoggerFactory.getLogger(InMemoryUserRepositoryTest.class);

    @Test
    void multi_threads_test() throws InterruptedException {
        // given
        int numThreads = 1000;
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
        final CountDownLatch saveLatch = new CountDownLatch(1);

        // when
        for (int i = 0; i < numThreads; i++) {
            final String user = Integer.toString(i);
            executorService.execute(
                () -> {
                    try {
                        saveLatch.await();
                        InMemoryUserRepository.save(new User(user, "pw", "email"));
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                });
        }

        saveLatch.countDown();

        if (!executorService.isTerminated()) {
            logger.info("terminating threads");
            executorService.awaitTermination(1, TimeUnit.SECONDS);
        }

        logger.info("terminating finished");
        executorService.shutdown();

        //then
        final List<User> allUsers = InMemoryUserRepository.findAll();
        final HashSet<User> distinctUser = new HashSet<>(allUsers);

        assertThat(allUsers).hasSize(distinctUser.size());
    }
}
