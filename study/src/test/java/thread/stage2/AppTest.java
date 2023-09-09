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
    @Test
    void test() throws Exception {
        final var NUMBER_OF_THREAD = 10;

        // Thread 객체 배열 생성 및 10개의 Thread 객체 생성하여 배열에 할당한다.
        var threads = new Thread[NUMBER_OF_THREAD];
        for (int i = 0; i < NUMBER_OF_THREAD; i++) {
            threads[i] = new Thread(() -> incrementIfOk(TestHttpUtils.send("/test")));
        }

        // Thread 객체의 start() 메서드를 호출하면, Thread를 생성하고, 해당 스레드에서 run()메서드를 실행한다.
        for (final var thread : threads) {
            thread.start();
            Thread.sleep(50);
        }

        // join() 메서드를 호출한 스레드가 join() 메서드를 실행한 스레드의 종료를 기다린다.
        for (final var thread : threads) {
            thread.join();
        }

        // count.intValue()는 실제 생성된 스레드의 갯수이다.
        assertThat(count.intValue()).isEqualTo(2);
    }

    private static void incrementIfOk(final HttpResponse<String> response) {
        // 요청이 200일때 AtomicInteger의 수량 증가
        if (response.statusCode() == 200) {
            count.incrementAndGet();
        }
    }
}
