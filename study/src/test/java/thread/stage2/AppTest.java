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
     accept count: backlog 크기 만큼 생성된 운영체제 단의 accept 큐 사이즈 3-way 핸드셰이크가 끝난 커넥션이 대기
     max-connections: socket.accept() 로 받을 수 있는 커넥션 수 accept큐에서 대기 중인 커넥션을 꺼내서 accpet() 받을 수 있는 최대 커넥션 수
     threads: 한번에 처리할 수 있는 최대 요청 개수 (즉 실제 생성되는 스레드 개수)
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
