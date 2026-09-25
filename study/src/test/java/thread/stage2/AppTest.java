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
     * 기존 application.yml 상황
     * server:
     *   tomcat:
     *     accept-count: 1
     *     max-connections: 1
     *     threads:
     *       min-spare: 2
     *       max: 2
     *
     * 발생 상황
     * 1. 테스트 thread3은 응답을 받음.
     * 2. 테스트 thread6은 request time out
     * 서버에서는 둘 다 응답함.
     *
     * 기존에는 accept-count=1 그리고 max-connections=1이라 Tomcat이 관리하는 커넥션이 1개 OS 대기큐에 1개
     * 커넥션을 끊는 설정이 별도로 없어, default로 keep-alive-timeout, max-keep-alive-requests가 설정된다.
     * 따라서 별도 요청이 없어도 keep-alive-timeout default동안 Tomcat이 해당 커넥션을 가지고 있는다.
     *
     * 이때, TestHttpUtils에서 send()에 timeout 1초를 걸어두었다.
     * 따라서 client쪽에서 1초 뒤 응답이 없으면 연결을 끊는다.
     * 이때, Client쪽에서 연결을 끊는 요청을 보내게 된다. thread3에 대한 연결을 끊겠다는 요청은 바로 도달하여 연결이 끊긴다.
     * 그러나 thread 6에대한 연결을 끊는 요청은, 그 전에 아직 send() 요청이 안읽혀서 연결이 바로 끊기지 않는다.
     * 그래서 send() 요청이 수행 되고나서 연결이 끊긴다.
     *
     * 해결방안
     * max-connections를 2로 올린다. max-threads 값도 2이기 때문에 가능하다.
     * */
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
