package org.apache.catalina.connector;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import org.apache.catalina.connector.TestClient.ResponseType;
import org.apache.catalina.controller.AbstractController;
import org.apache.catalina.route.DefaultDispatcher;
import org.apache.catalina.route.RequestMapper;
import org.apache.catalina.route.RequestMapping;
import org.apache.coyote.Dispatcher;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ConnectorTest {

    private static final Map<ResponseType, AtomicInteger> result = Map.of(
            ResponseType.SUCCESS, new AtomicInteger(0),
            ResponseType.SUCCESS_QUEUED, new AtomicInteger(0),
            ResponseType.FAILED, new AtomicInteger(0)
    );

    @Disabled("시간이 오래 소요되는 테스트")
    @DisplayName("동시 요청 시 최대로 동시에 처리할 수 있는 요청 수와 대기 수를 제한할 수 있다.")
    @Test
    void concurrentRequestTest() throws InterruptedException {
        // given
        int acceptCount = 100;
        int maxThreads = 250;
        int requestCount = 500;
        int simulatingDelay = 10000;  // 충분히 커야 스레드 생성 오버헤드로 인한 오차를 줄일 수 있음

        RequestMapping requestMapping = new RequestMapper();
        requestMapping.register(createTestController(simulatingDelay));
        Dispatcher dispatcher = new DefaultDispatcher(requestMapping);
        Connector connector = new Connector(8080, acceptCount, maxThreads);
        connector.start(dispatcher);

        // when
        /*
          0. 500개 요청
          1. 기본 스레드 150개로 바로 150개 요청처리 (success)
          2. 이후에 수신되는 100개는 대기 후 처리 (success_queued)
          3. 큐가 가득 찬 이후 진입하는 100개(maxThread-150)는 추가 스레드 생성 후 처리 (success)
          4. 나머지 150개는 요청 거절 (failed)
          서버에서 모든 요청을 accept()까지는 해 주고 있으므로,
          (4)는 Connection Timeout이 발생하진 않지만 서버에서 소켓을 먼저 닫아서 EOFException || SocketException 발생
         */
        List<Thread> threads = IntStream.range(0, requestCount)
                .mapToObj(i -> new Thread(() -> processResponse(TestClient.send("/delay", i + 1))))
                .toList();

        int tid = 1;
        for (Thread t : threads) {
            Thread.sleep(10);
            System.out.println(tid++ + " 번 요청 전송");
            t.start();
        }

        for (Thread t : threads) {
            t.join();
        }

        // then
        result.forEach((key, value) -> System.out.println(key + ": " + value.get()));
        assertAll(
                () -> assertThat(result.get(ResponseType.SUCCESS).get()).isEqualTo(maxThreads),
                () -> assertThat(result.get(ResponseType.SUCCESS_QUEUED).get()).isEqualTo(acceptCount),
                () -> assertThat(result.get(ResponseType.FAILED).get()).isEqualTo(
                        requestCount - (maxThreads + acceptCount))
        );
    }

    @SuppressWarnings("SameParameterValue")
    private AbstractController createTestController(long delayInMillis) {
        return new AbstractController() {
            @Override
            public String matchedPath() {
                return "/delay";
            }

            @Override
            @SuppressWarnings("CallToPrintStackTrace")
            public void doGet(HttpRequest request, HttpResponse response) {
                try {
                    String tid = request.getHeaders().get("id");
                    System.out.println(tid + " 번 요청 처리 시작");
                    Thread.sleep(delayInMillis);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private static void processResponse(SimpleEntry<ResponseType, java.net.http.HttpResponse<String>> response) {
        if (response == null) {
            return;
        }
        result.get(response.getKey()).incrementAndGet();
    }
}
