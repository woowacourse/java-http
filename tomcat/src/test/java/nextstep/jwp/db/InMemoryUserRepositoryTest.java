package nextstep.jwp.db;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import nextstep.jwp.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class InMemoryUserRepositoryTest {

    private InMemoryUserRepository inMemoryUserRepository;

    @BeforeEach
    void tearDown() {
        inMemoryUserRepository = InMemoryUserRepository.getInstance();
        inMemoryUserRepository.deleteAll();
    }

    @Test
    void 회원을_저장한다() {
        // given
        User user = new User("huchu", "huchu123", "huchu@naver.com");

        // when
        Long id = inMemoryUserRepository.save(user);

        // then
        assertThat(id).isNotNull();
    }

    @Test
    void 계정으로_회원을_조회한다() {
        // given
        User user = new User("huchu", "huchu123", "huchu@naver.com");
        inMemoryUserRepository.save(user);

        // when
        User foundUser = inMemoryUserRepository.findByAccount("huchu").get();

        // then
        assertThat(foundUser).isEqualTo(user);
    }

    @Test
    void 모든_회원을_조회한다() {
        // given
        User user = new User("huchu", "huchu123", "huchu@naver.com");
        inMemoryUserRepository.save(user);

        // when
        List<User> users = inMemoryUserRepository.findAll();

        // then
        assertThat(users).hasSize(1);
    }

    @Test
    void 모든_회원을_삭제한다() {
        // given
        User user = new User("huchu", "huchu123", "huchu@naver.com");
        inMemoryUserRepository.save(user);

        // when
        inMemoryUserRepository.deleteAll();

        // then
        assertThat(inMemoryUserRepository.findAll()).isEmpty();
    }

    @Test
    void 멀티스레드_테스트() throws InterruptedException {
        // given
        int numThreads = 3;
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);

        // when
        for (int i = 0; i < 1000; i++) {
            executorService.submit(
                    () -> inMemoryUserRepository.save(new User("huchu1", "huchu123", "huchu@naver.com")));
            executorService.submit(
                    () -> inMemoryUserRepository.save(new User("huchu2", "huchu123", "huchu@naver.com")));
            executorService.submit(
                    () -> inMemoryUserRepository.save(new User("huchu3", "huchu123", "huchu@naver.com")));
        }

        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);

        // then
        long memberIdCount = inMemoryUserRepository.findAll().stream()
                .map(User::id)
                .distinct()
                .count();
        assertThat(memberIdCount).isEqualTo(3000);
    }
}
