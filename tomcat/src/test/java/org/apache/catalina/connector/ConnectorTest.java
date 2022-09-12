package org.apache.catalina.connector;

import static org.apache.HttpTestSupporter.requestGET;
import static org.assertj.core.api.Assertions.assertThat;

import java.net.http.HttpResponse;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/*
    acceptCount와 maxThreads의 크기를 변경해가면서,
    서버가 요청을 몇개까지 처리할 수 있는지 테스트한다.

    acceptCount: 사용 가능한 스레드가 없을 경우 대기 가능 요청 수
    maxThreads: 스레드 풀 크기
    -> 최소 처리 가능 요청 수 = acceptCount() + maxThreads

    Q. 최대 ThradPool의 크기는 250, 모든 Thread가 사용 중인(Busy) 상태이면 100명까지 대기 상태로 만들려면 어떻게 할까?
    A. maxThreads = 250, acceptCount = 100
 */
class ConnectorTest {

    private AtomicInteger count;
    private int port;

    @BeforeEach
    public void setUp() {
        count = new AtomicInteger(0);
        port = ThreadLocalRandom.current().nextInt(49152, 65536);
    }

    @ParameterizedTest
    @CsvSource({
            "100, 200, 200",
            "100, 250, 300",
            "150, 200, 300"
    })
    void 최소_처리_가능_요청_수_이하이면_모두_처리할_수_있는지_테스트한다(final int acceptCount, final int maxThreads,
                                          final int numberOfRequest) throws Exception {
        // given
        서버의_연결을_시작한다(acceptCount, maxThreads);

        // when
        여러개의_요청을_보낸다(numberOfRequest);

        // then
        assertThat(count.intValue()).isEqualTo(numberOfRequest);
    }

    @ParameterizedTest
    @CsvSource({
            "100, 200, 350",
            "100, 250, 400",
            "150, 200, 400"
    })
    void 최소_처리_가능_요청_수_이상_처리할_수_있는지_테스트한다(final int acceptCount, final int maxThreads,
                                              final int numberOfRequest) throws Exception {
        // given
        서버의_연결을_시작한다(acceptCount, maxThreads);

        // when
        여러개의_요청을_보낸다(numberOfRequest);

        // then
        assertThat(count.intValue()).isBetween(acceptCount + maxThreads, numberOfRequest);
    }

    private void 서버의_연결을_시작한다(final int acceptCount, final int maxThreads) {
        final var connector = new Connector(port, acceptCount, maxThreads);
        connector.start();
    }

    private void 여러개의_요청을_보낸다(final int numberOfRequest) throws InterruptedException {
        final var threads = new Thread[numberOfRequest];

        for (int i = 0; i < numberOfRequest; i++) {
            threads[i] = new Thread(() -> incrementIfOk(count, requestGET(port, "/")));
        }

        for (final var thread : threads) {
            thread.start();
            Thread.sleep(100);
        }

        for (final var thread : threads) {
            thread.join();
        }
    }

    private void incrementIfOk(final AtomicInteger count, final HttpResponse<String> response) {
        if (response.statusCode() == 200) {
            count.incrementAndGet();
        }
    }
}
