package nextstep.app;

import nextstep.jwp.dto.UserRegisterRequest;
import org.junit.jupiter.api.Test;

class ApplicationTest {

    @Test
    void post() throws Exception {
        final var NUMBER_OF_THREAD = 10;
        var threads = new Thread[NUMBER_OF_THREAD];

        for (int i = 0; i < NUMBER_OF_THREAD; i++) {
            threads[i] = new Thread(() -> TestHttpUtils.send("/index.html"));
        }

        for (final var thread : threads) {
            thread.start();
            Thread.sleep(10);
        }

        for (final var thread : threads) {
            thread.join();
        }
    }

    @Test
    void postLogin() throws Exception {
        final var NUMBER_OF_THREAD = 10;
        var threads = new Thread[NUMBER_OF_THREAD];
        final UserRegisterRequest user = new UserRegisterRequest("dongho108",
                "1234",
                "dongho108@naver.com");

        for (int i = 0; i < NUMBER_OF_THREAD; i++) {
            threads[i] = new Thread(() -> TestHttpUtils.postRegister(user));
        }

        for (final var thread : threads) {
            thread.start();
            Thread.sleep(10);
        }

        for (final var thread : threads) {
            thread.join();
        }
    }
}