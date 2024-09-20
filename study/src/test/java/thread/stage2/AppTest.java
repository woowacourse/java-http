package thread.stage2;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.http.HttpResponse;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

class AppTest {

    // 200 응답을 받은 클라이언트의 수
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
    @Test
    void test() throws Exception {
        /**
         * 이 스레드들은 톰캣이 요청을 처리할 때 사용하는 스레드와 관계가 없다고 이해.
         * 단순히 각기 다른 클라이언트에서 50ms 간격으로 병렬로 요청을 보내기 위함.
         */
        final var NUMBER_OF_THREAD = 10;
        var threads = new Thread[NUMBER_OF_THREAD];

        for (int i = 0; i < NUMBER_OF_THREAD; i++) {
            threads[i] = new Thread(() -> incrementIfOk(TestHttpUtils.send("/test")));
        }

        /**
         * NUMBER_OF_THREAD 개의 클라이언트가 순차적으로 Connection 연결 요청을 보냄.
         * max-connection 개수 만큼의 클라이언트 요청이 처리되고, accept-count 만큼의 클라이언트 요청은 큐에서 대기함
         * 이때 나머지 (NUMBER_OF_THREAD - (max-connection + accept-count)) 만큼의 요청은 refuse됨
         */
        for (final var thread : threads) {
            thread.start();
            System.out.println(thread.getName() + " started!!!!!");
            Thread.sleep(50);
        }

        /**
         * 모든 스레드들의 작업이 완료될 때 까지 TestWorker 스레드를 대기시키기 위함
         * 즉, 아래 assert문이 스레드 작업이 모두 완료된 후에 실행됨을 보장하기 위함
         */
        for (final var thread : threads) {
            thread.join();
            System.out.println(thread.getName() + " joined!!!!!");
        }

        assertThat(count.intValue()).isEqualTo(2);
    }

    private static void incrementIfOk(final HttpResponse<String> response) {
        if (response.statusCode() == 200) {
            count.incrementAndGet();
            System.out.println(Thread.currentThread().getName() + ": request succeed");
        }
    }
}
