import static org.assertj.core.api.Assertions.assertThat;

import java.net.http.HttpResponse;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.TestHttpUtils;

class ThreadTest {

    private static final AtomicInteger httpCallCount = new AtomicInteger(0);

    @Test
    @DisplayName("여러 스레드가 동시에 회원가입을 진행할 경우 한 스레드만 302 Found 응답을 받는다.")
    void multiThreadRegistrationTest() throws InterruptedException {
        // Given
        String path = "/register";
        String body = "account=gugu2&password=password&email=hkkang2@woowahan.com";
        String contentType = "x-www-form-urlencoded";

        // When
        int NUMBER_OF_THREADS = 10;
        Thread[] threads = new Thread[NUMBER_OF_THREADS];

        for (int i = 0; i < NUMBER_OF_THREADS; i++) {
            threads[i] = new Thread(() -> incrementIfFound(TestHttpUtils.sendPost(path, contentType, body)));
        }

        for (Thread thread : threads) {
            thread.start();
            Thread.sleep(50);
        }

        for (Thread thread : threads) {
            thread.join();
        }

        // Then
        assertThat(httpCallCount.get()).isEqualTo(1);
    }

    private static void incrementIfFound(final HttpResponse<Void> response) {
        if (response.statusCode() == 302) {
            httpCallCount.incrementAndGet();
        }
    }
}
