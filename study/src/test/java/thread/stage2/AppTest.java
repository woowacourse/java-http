package thread.stage2;

import org.junit.jupiter.api.Test;

import java.net.http.HttpResponse;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

class AppTest {

    /*
    AtomicInteger는 자바에서 동시성 환경에서 정수 값을 안전하게 변경하기 위해 사용하는 클래스.
    여러 스레드가 동시에 접근해도 값을 올바르게 증가/감소시킬 수 있도록 원자적 연산을 제공.
    즉, 이 객체를 사용하면 synchronized 없이도 스레드 안전하게 카운터 증가·감소가 가능
     */
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
        final var NUMBER_OF_THREAD = 10; // 10개의 동시 요청
        var threads = new Thread[NUMBER_OF_THREAD];

        // 10개의 스레드가 0.05초 간격으로 각각 /test 엔드포인트에 HTTP 요청을 보냄. 응답 코드가 200이라면 count++
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

        /*
        2로 기대한 이유
        첫 번째 요청은 즉각 처리(max-connections).
        두 번째 요청은 운영체제 수준 큐(accept-count)에서 대기했다가 바로 처리.
        세 번째 이후의 요청은 대기 큐가 가득 차서(accept-count=1) 바로 거절되거나 Connection Refused가 발생하며, 아예 처리되지 않음.
        → 즉, 한번에 1개만 처리 + 1개 대기만 허용. 총 2개의 요청만 받아들이고 처리할 수 있다.
         */
        assertThat(count.intValue()).isEqualTo(2);
    }

    private static void incrementIfOk(final HttpResponse<String> response) {
        if (response.statusCode() == 200) {
            count.incrementAndGet();
        }
    }
}

/*
handlebars:
  suffix: .html

server:
  tomcat:
    accept-count: 1 // max-connections를 초과했을 때, 추가적으로 운영체제 큐에서 대기할 수 있는 연결의 최대 개수. 1로 설정하면 1개만 추가 대기가 가능하고 그 이후 요청은 거절됨
    max-connections: 1 // 동시에 서버가 accept하고 처리할 수 있는 최대 connection 수. 1로 설정하면, 단 1개의 연결만 처리 가능.
    threads:
      min-spare: 2 // Tomcat의 스레드 풀에서 항상 유지할 최소 대기 스레드 개수를 설정하는 값
      max: 2 // thread.max: 동시에 요청을 처리할 최대 thread 수. 여기서는 2로 설정.


// 성공 세팅
handlebars:
  suffix: .html

server:
  tomcat:
    accept-count: 0
    max-connections: 2
    threads:
      min-spare: 2
      max: 2


 */

/*
1단계 기본 설정 결과:

application.yml의 현재 설정값: threads.max = 2, accept-count = 1, max-connections = 1
테스트 결과: 실패
App 콘솔에 출력된 "http call count" 숫자:
assertThat에서 예상한 값(2)과 실제 결과의 일치 여부: 1

threads.max를 5로 변경했을 때:
테스트 결과: 실패
App 콘솔의 "http call count" 숫자: 1 2
기본 설정 대비 변화: 동일

accept-count를 5로 변경했을 때:
테스트 결과: 실패
App 콘솔의 "http call count" 숫자: 1 2 3 4 5 6
로그 출력 시간 간격: 순차적 간격

max-connections를 5로 변경했을 때:
테스트 결과: 실패
App 콘솔의 "http call count" 숫자: 3
이전 두 실험과 비교한 결과: 로그 출력 거의 동시에

 */