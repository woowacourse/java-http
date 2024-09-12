package thread.stage2;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.http.HttpResponse;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

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
     * accept-count: 1 ➡️ 톰캣이 처리할 수 있는 최대 대기 큐 크기
     * max-connections: 1 ➡️ 톰캣이 허용할 수 있는 최대 동시 연결 수
     * threads:
     * max: 2
     * 2024-09-13T02:43:11.462+09:00  INFO 69799 --- [nio-8080-exec-1] thread.stage2.SampleController           : http call count : 1
     * 2024-09-13T02:43:13.250+09:00  INFO 69799 --- [nio-8080-exec-1] thread.stage2.SampleController           : http call count : 2
     * <p>
     * accept-count: 5
     * max-connections: 1
     * threads:
     * max: 2
     * 2024-09-13T02:49:50.505+09:00  INFO 70353 --- [nio-8080-exec-1] thread.stage2.SampleController           : http call count : 1
     * 2024-09-13T02:49:52.335+09:00  INFO 70353 --- [nio-8080-exec-1] thread.stage2.SampleController           : http call count : 2
     * 2024-09-13T02:49:52.846+09:00  INFO 70353 --- [nio-8080-exec-2] thread.stage2.SampleController           : http call count : 3
     * 2024-09-13T02:49:53.355+09:00  INFO 70353 --- [nio-8080-exec-1] thread.stage2.SampleController           : http call count : 4
     * 2024-09-13T02:49:53.864+09:00  INFO 70353 --- [nio-8080-exec-2] thread.stage2.SampleController           : http call count : 5
     * 2024-09-13T02:49:54.373+09:00  INFO 70353 --- [nio-8080-exec-1] thread.stage2.SampleController           : http call count : 6
     * 💡 처음에 요청 하나 받음(max-connections: 1) 스레드가 3개 있지만 요청을 한 번에 하나만 처리할 수 있기 때문에 실행 시간도 다 다르다.
     * 그리고 큐에 5개의 요청을 보관할 수 있기 때문에 처음에 받은 요청 1 + 큐에 있던 요청 5개 = 총 6개 처리 가능하다.
     *
     *     accept-count: 5
     *     max-connections: 2
     *     threads:
     *       max: 2
     * 2024-09-13T03:09:14.090+09:00  INFO 71843 --- [nio-8080-exec-1] thread.stage2.SampleController           : http call count : 1
     * 2024-09-13T03:09:14.090+09:00  INFO 71843 --- [nio-8080-exec-2] thread.stage2.SampleController           : http call count : 2
     * 2024-09-13T03:09:15.893+09:00  INFO 71843 --- [nio-8080-exec-2] thread.stage2.SampleController           : http call count : 3
     * 2024-09-13T03:09:15.893+09:00  INFO 71843 --- [nio-8080-exec-1] thread.stage2.SampleController           : http call count : 4
     * 2024-09-13T03:09:16.401+09:00  INFO 71843 --- [nio-8080-exec-1] thread.stage2.SampleController           : http call count : 5
     * 2024-09-13T03:09:16.403+09:00  INFO 71843 --- [nio-8080-exec-2] thread.stage2.SampleController           : http call count : 6
     * 2024-09-13T03:09:16.908+09:00  INFO 71843 --- [nio-8080-exec-1] thread.stage2.SampleController           : http call count : 7
     * 💡 max-connections를 2로 바꾸면 한 번에 두 개씩 처리할 수 있다 그래서 두 요청들이 처리된 시간이 같다.
     * 처음에 받은 요청 2 + 큐에 있던 요청 5 = 총 7개 처리 가능하다.
     *
     *     accept-count: 5
     *     max-connections: 1
     *     threads:
     *       max: 5
     * 2024-09-13T03:10:27.652+09:00  INFO 71939 --- [nio-8080-exec-3] thread.stage2.SampleController           : http call count : 2
     * 2024-09-13T03:10:26.052+09:00  INFO 71939 --- [nio-8080-exec-4] thread.stage2.SampleController           : http call count : 3
     * 2024-09-13T03:10:26.562+09:00  INFO 71939 --- [nio-8080-exec-5] thread.stage2.SampleController           : http call count : 4
     * 2024-09-13T03:10:27.069+09:00  INFO 71939 --- [nio-8080-exec-1] thread.stage2.SampleController           : http call count : 5
     * 2024-09-13T03:10:27.579+09:00  INFO 71939 --- [nio-8080-exec-2] thread.stage2.SampleController           : http call count : 6
     * 💡 스레드가 많아도 한 번에 처리할 수 있는 요청이 하나라서 6개만 처리 가능하다.
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
