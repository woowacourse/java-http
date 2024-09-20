package thread.stage2;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    @Test
    void test() throws Exception {
        //accept-count : 한번에 받아들이는 개수 ( default 100 개 )
        //max-connections : 수립가능한 Connection 총 개수 ( 이거 단위로 계속 끊기는 듯? )

        // 스레드가 늘어나도 max-connections 가 안늘어나면 동일하다.
        // 스레드 8 이여도 max-connections 가 2이면 2개씩 작동 그래서 max-connections 가 큰이유

        // 4 1 2 2 이면? = 6개 ( 연결되어있는거 말고 대기를 받는거 )

        // 1 2 2 2 이면? = 4개

        // TestHTTP Utils 의 requestTimeOut 을 늘리면, 커넥션을 유지하고 있어서 더 못받는다.

        // 2 2 2 3 = 4개 ( max-connections 가 더 작아서 max가 3이여도 2개씩 )

        // 2 3 2 3 = 5개

        final var NUMBER_OF_THREAD = 10;
        var threads = new Thread[NUMBER_OF_THREAD];

        for (int i = 0; i < NUMBER_OF_THREAD; i++) {
            threads[i] = new Thread(() -> incrementIfOk(TestHttpUtils.send("/test")));
        }

        for (final var thread : threads) {
            thread.start();

            // Wait VS Sleep
            // wait 는 특정 객체간 락을 사용해서 스레드 간 통신 관리에 사용한다. ( synchronized 내부에서만 사용해야 함 )
            // notify 를 사용해서 깨운다.
            // sleep 는 해당 스레드를 단순히 일정 시간 동안 대기 시키고 작업을 수행하는 것
            // => 완전 다른 용도! ( wait : 하나의 스레드가 다른 스레드 작업 완료를 기다려야 할때 사용 )
            Thread.sleep(50);
        }

        for (final var thread : threads) {
            thread.join();
        }

        assertThat(count.intValue()).isEqualTo(10);
    }

    private static void incrementIfOk(final HttpResponse<String> response) {
        if (response.statusCode() == 200) {
            count.incrementAndGet();
        }
    }
}
