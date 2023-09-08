package thread.stage2;

import org.junit.jupiter.api.Test;

import java.net.http.HttpResponse;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

class AppTest {

    private static final AtomicInteger count = new AtomicInteger(0);

    /**
     * 1. App 클래스의 애플리케이션을 실행시켜 서버를 띄운다. <br/>
     * 2. 아래 테스트를 실행시킨다. <br/>
     * 3. AppTest가 아닌 App의 콘솔에서 SampleController가 생성한 http call count 로그를 확인한다. <br/>
     * 4. application.yml에서 설정값을 변경해보면서 어떤 차이점이 있는지 분석해본다. <br/>
     * - 로그가 찍힌 시간 <br/>
     * - 스레드명(nio-8080-exec-x)으로 생성된 스레드 갯수를 파악 <br/>
     * - http call count <br/>
     * - 테스트 결과값
     * <p/>
     * [결과] <br/>
     * 1. accept-count : 대기열 큐의 길이 (default: 100) <br/>
     * 2. max-connections : 한 번에 최대로 연결할 수 있는 요청의 수 (default: 8192) <br/>
     * 3. threads.max: 동시에 처리할 수 있는 최대 수 (default: 200) <br/>
     * <p/>
     * [NUMBER_OF_THREAD, accept-count, max-connections, threads.max] <br/>
     * <br/>
     * <br/>
     * [250, 1, 10, 200] - 모두 수행하지 못함 (243개 - max connections 부족)
     * [250, 1, 10, 500] - 모두 수행하지 못함 (248개 - max connections 부족)
     * [250, 1, 100, 250] - 모두 처리
     * [250, 1, 100, 100] - 모두 처리
     * [250, 1, 100, 1] -  모두 처리하지 못함 (128개 - threads.max 부족, 0.5초 처리)
     * [250, 200, 100, 1] - 모두 처리하지 못함 (246개 - threads.max 부족)
     * [250, 500, 1, 100] - 모두 처리하지 못함 (130개 - Error setting socket options 에러 나옴)
     * [250, 1, 10, 100] - 모두 처리하지 못함 (246개 - accept-count, max-connections)
     */
    @Test
    void test() throws Exception {
        final var NUMBER_OF_THREAD = 250;
        var threads = new Thread[NUMBER_OF_THREAD];

        for (int i = 0; i < NUMBER_OF_THREAD; i++) {
            threads[i] = new Thread(() -> incrementIfOk(TestHttpUtils.send("/test")));
        }

        for (final var thread : threads) {
            thread.start();
            Thread.sleep(50);
        }

        for (final var thread : threads) {
            thread.join();
        }

        assertThat(count.intValue()).isEqualTo(2);
    }

    private static void incrementIfOk(final HttpResponse<String> response) {
        if (response.statusCode() == 200) {
            count.incrementAndGet();
        }
    }
}
