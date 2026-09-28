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
     * 첫번째 결과
     *
     * server:
     *   tomcat:
     *     accept-count: 1
     *     max-connections: 1
     *     threads:
     *       min-spare: 2
     *       max: 2
     *
     * 2026-09-27T16:46:50.332+09:00  INFO 17323 --- [nio-8080-exec-1] o.s.web.servlet.DispatcherServlet        : Completed initialization in 0 ms
     * 2026-09-27T16:46:50.850+09:00  INFO 17323 --- [nio-8080-exec-1] thread.stage2.SampleController           : http call count : 1
     * 2026-09-27T16:46:52.624+09:00  INFO 17323 --- [nio-8080-exec-1] thread.stage2.SampleController           : http call count : 2
     *
     */

    /**
     * 두 번째 테스트
     *
     * server:
     *   tomcat:
     *     accept-count: 2
     *     max-connections: 1
     *     threads:
     *       min-spare: 2
     *       max: 2
     *
     * 2026-09-27T16:51:43.340+09:00  INFO 18714 --- [nio-8080-exec-1] thread.stage2.SampleController           : http call count : 1
     * 2026-09-27T16:51:45.155+09:00  INFO 18714 --- [nio-8080-exec-1] thread.stage2.SampleController           : http call count : 2
     * 2026-09-27T16:51:45.662+09:00  INFO 18714 --- [nio-8080-exec-2] thread.stage2.SampleController           : http call count : 3
     *
     */

    /**
     * 세번째 테스트
     *
     * server:
     *   tomcat:
     *     accept-count: 10
     *     max-connections: 2
     *     threads:
     *       min-spare: 2
     *       max: 2
     *
     * 2026-09-27T16:53:17.235+09:00  INFO 19204 --- [nio-8080-exec-2] thread.stage2.SampleController           : http call count : 1
     * 2026-09-27T16:53:17.239+09:00  INFO 19204 --- [nio-8080-exec-1] thread.stage2.SampleController           : http call count : 2
     * 2026-09-27T16:53:19.044+09:00  INFO 19204 --- [nio-8080-exec-1] thread.stage2.SampleController           : http call count : 4
     * 2026-09-27T16:53:19.044+09:00  INFO 19204 --- [nio-8080-exec-2] thread.stage2.SampleController           : http call count : 3
     * 2026-09-27T16:53:19.551+09:00  INFO 19204 --- [nio-8080-exec-2] thread.stage2.SampleController           : http call count : 5
     * 2026-09-27T16:53:19.553+09:00  INFO 19204 --- [nio-8080-exec-1] thread.stage2.SampleController           : http call count : 6
     * 2026-09-27T16:53:20.059+09:00  INFO 19204 --- [nio-8080-exec-2] thread.stage2.SampleController           : http call count : 7
     * 2026-09-27T16:53:20.060+09:00  INFO 19204 --- [nio-8080-exec-1] thread.stage2.SampleController           : http call count : 8
     * 2026-09-27T16:53:20.564+09:00  INFO 19204 --- [nio-8080-exec-2] thread.stage2.SampleController           : http call count : 9
     * 2026-09-27T16:53:20.566+09:00  INFO 19204 --- [nio-8080-exec-1] thread.stage2.SampleController           : http call count : 10
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

        assertThat(count.intValue()).isEqualTo(2);
    }

    private static void incrementIfOk(final HttpResponse<String> response) {
        if (response.statusCode() == 200) {
            count.incrementAndGet();
        }
    }
}
