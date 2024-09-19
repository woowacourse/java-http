package thread.stage2;

import org.junit.jupiter.api.Test;

import java.net.http.HttpResponse;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

class AppTest {

    private static final AtomicInteger count = new AtomicInteger(0);

    /**
     * 1. App 클래스의 애플리케이션을 실행시켜 서버를 띄운다.
     * 2. 아래 테스트를 실행시킨다.
     * 3. AppTest가 아닌 App의 콘솔에서 SampleController가 생성한 http call count 로그를 확인한다.
     * 4. application.yml에서 설정값을 변경해보면서 어떤 차이점이 있는지 분석해본다.
     * - 로그가 찍힌 시간
     * - 스레드명(nio-8080-exec-x)으로 생성된 스레드 갯수를 파악
     * - http call count
     * - 테스트 결과값
     */
    /**
     *   tomcat:
     *     threads:
     *       max: 200 # 생성할 수 있는 thread의 총 개수
     *       min-spare: 10 # 항상 활성화 되어있는(idle) thread의 개수
     *     max-connections: 8192 # 수립가능한 connection의 총 개수
     *     accept-count: 100 # 작업큐의 사이즈
     *     connection-timeout: 20000 # timeout 판단 기준 시간, 20초
     *
     * max-connections != threads.max?
     * -> NIO connectors
     *
     * min-spare만큼의 thread는 항상 활성화되어있다.
     * 대기 큐(accept-count)가 꽉 찬 상태에서 요청이 더 들어올 때마다 thread를 추가 생성한다.
     * 이때, 추가 생성 가능한 thread의 수는 threads.max만큼이다.
     */
    @Test
    void test() throws Exception {
        final var NUMBER_OF_THREAD = 10;
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
        //max-connections:2 -> 요청을 2개까지만 받을 수 있다. 커넥션 2개가 차지되어있는 동안 들어온 요청은 무시된다.
        assertThat(count.intValue()).isEqualTo(2);
    }

    private static void incrementIfOk(final HttpResponse<String> response) {
        if (response.statusCode() == 200) {
            count.incrementAndGet();
        }
    }
}
