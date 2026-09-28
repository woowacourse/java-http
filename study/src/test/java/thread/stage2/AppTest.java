package thread.stage2;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.http.HttpResponse;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

class AppTest {

    private static final AtomicInteger count = new AtomicInteger(0);

    /**
     * 1. App 클래스의 애플리케이션을 실행시켜 서버를 띄운다. 2. 아래 테스트를 실행시킨다. 3. AppTest가 아닌 App의 콘솔에서 SampleController가 생성한 http call
     * count 로그를 확인한다. 4. application.yml에서 설정값을 변경해보면서 어떤 차이점이 있는지 분석해본다. - 로그가 찍힌 시간 - 스레드명(nio-8080-exec-x)으로 생성된 스레드
     * 갯수를 파악 - http call count - 테스트 결과값
     */
    @Test
    void test() throws Exception {
        final var NUMBER_OF_THREAD = 10;
        var threads = new Thread[NUMBER_OF_THREAD];

        // 요청 응답이 200이면, count를 증가시키는 동작을수행할 스레드 선언
        for (int i = 0; i < NUMBER_OF_THREAD; i++) {
            threads[i] = new Thread(() -> incrementIfOk(TestHttpUtils.send("/test")));
        }

        // 각 스레드 요청 수행
        for (final var thread : threads) {
            thread.start();
            Thread.sleep(50);
        }

        // 스레드가 끝날 때까지 대기
        for (final var thread : threads) {
            thread.join();
        }

        // 테스트 성공 조건: OK로 응답받은 개수가 2개여야 한다.
        // 1. SampleController는 요청 후 0.5초 대기하고 응답한다 (=스레드 하나당 최소 0.5초가 소모된다)
        // 2. TestHttpUtils에서 Http 요청을 보낼 때 timeout을 1초로 설정한다.
        // 3. App의 max-connection이 2라면, 최초 2개 스레드는 바로 처리되어 0.5초 안에 응답이 가능하다.
        // 4. 나머지 응답은 최대 accept-count 개수까지 운영체제 TCP 소켓에서 대기하다가, 앞선 연결이 끝나면, 스레드가 순차적으로 할당된다.
        // 5. 스레드 10개 요청이 순차적으로 처리되지만, TestHttpUtils에서 설정한 timeout이 한참 지났으므로, 실패처리된다.
        // 6. 결과적으로 1초 내에 응답하는 스레드는 2개가 된다.
        assertThat(count.intValue()).isEqualTo(2);
    }

    private static void incrementIfOk(final HttpResponse<String> response) {
        if (response.statusCode() == 200) {
            count.incrementAndGet();
        }
    }
}
