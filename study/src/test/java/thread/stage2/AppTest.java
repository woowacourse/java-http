package thread.stage2;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.http.HttpResponse;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

class AppTest {

    private static final AtomicInteger count = new AtomicInteger(0);

    /*
    1. App 클래스의 애플리케이션을 실행시켜 서버를 띄운다.
    2. 아래 테스트를 실행시킨다.
    3. AppTest가 아닌 App의 콘솔에서 SampleController가 생성한 http call count 로그를 확인한다.
    - 2024-09-13T12:38:49.359+09:00  INFO 3336 --- [nio-8080-exec-1] thread.stage2.SampleController : http call count : 1
    - 2024-09-13T12:38:51.056+09:00  INFO 3336 --- [nio-8080-exec-1] thread.stage2.SampleController : http call count : 2
    4. application.yml에서 설정값을 변경해보면서 어떤 차이점이 있는지 분석해본다.
    - 로그가 찍힌 시간
    - 스레드명(nio-8080-exec-x)으로 생성된 스레드 갯수를 파악
    - http call count
    - 테스트 결과값

    최대 스레드 수 (threads.max - 2개) 초과한 요청 들어오면 대기열에 들어감.
    대기열엔 최대 (accept-count - 1개) 만큼의 요청 들어갈 수 있음.
    서버가 부하를 받을 땐 더 많은 스레드를 준비하도록 하는데,
    이때 요청을 처리하기 위해 대기하는 최소 스레드는 (threads.min-spare - 2개) 임.
    서버가 동시에 처리할 수 있는 연결 수는 최대 (max-connections - 2개) 임

    해당 테스트애선 나중에 실행되는 대기열에 있는 친구들은 측정은 안됨
    */
    @Test
    void test() throws Exception {
        final var NUMBER_OF_THREAD = 10;
        var threads = new Thread[NUMBER_OF_THREAD];

        for (int i = 0; i < NUMBER_OF_THREAD; i++) {
            threads[i] = new Thread(() -> incrementIfOk(TestHttpUtils.send("/test")));
        }

        for (final var thread : threads) {
            thread.start(); // 각 스레드는 50ms 지연 두고 실행됨
            Thread.sleep(50); // 동시에 모든 스레드가 실행되지 않게 하기 위함
        }

        for (final var thread : threads) {
            thread.join(); // 모든 스레드가 완료될 때까지 대기
        }

        assertThat(count.intValue()).isEqualTo(10); //  10개 요청 중 성공한 요청의 개수
    }

    private static void incrementIfOk(final HttpResponse<String> response) {
        System.out.println("response = " + response.statusCode());
        if (response.statusCode() == 200) {
            count.incrementAndGet();
        }
    }
}
