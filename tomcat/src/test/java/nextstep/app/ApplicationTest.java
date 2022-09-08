package nextstep.app;

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
}