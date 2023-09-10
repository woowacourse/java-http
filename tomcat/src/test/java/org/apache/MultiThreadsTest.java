package org.apache;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class MultiThreadsTest {

    @Test
    void test() throws Exception {
        int numThreads = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);

        for (int i = 0; i < numThreads; i++) {
            executorService.submit(() -> TestHttpUtils.sendRegister(
                    "huchu",
                    "huchu123",
                    "huchu@naver.com"
            ));
        }

        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);

        List<User> users = InMemoryUserRepository.findAll();

        // 왜 Test 가 실패할까
        // 왜 huchu 가 없을까
        assertThat(users).hasSize(2);
    }
}
