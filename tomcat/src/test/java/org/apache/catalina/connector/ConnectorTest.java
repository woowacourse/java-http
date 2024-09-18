package org.apache.catalina.connector;

import static org.assertj.core.api.Assertions.assertThat;

import com.techcourse.controller.ThreadTestController;
import java.net.http.HttpResponse;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.catalina.controller.RequestMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ConnectorTest {

    private static final AtomicInteger count = new AtomicInteger(0);

    @BeforeEach
    void setUp() {
        RequestMapper requestMapper = RequestMapper.getInstance();
        requestMapper.addMapping("/test", new ThreadTestController());
    }

    // 해당 테스트가 성공하려면 tomcat/src/main/java/com/techcourse/Application.java 를 먼저 실행시켜야 합니다.

    @DisplayName("201개의 스레드에서 GET 요청을 보내면 200개의 OK 응답을 받는다.")
    @Test
    void thread() throws InterruptedException {
        int maxThreads = 200;
        Thread[] threads = new Thread[maxThreads + 1];

        // 201개의 스레드 배열을 등록한다.
        for (int i = 0; i < maxThreads + 1; i++) {
            threads[i] = new Thread(() -> incrementIfOk(TestHttpUtils.send("/test")));
        }

        // 201개의 스레드를 시작한다.
        for (Thread thread : threads) {
            thread.start();
            Thread.sleep(5);
        }

        // 201개의 스레드가 끝나기를 기다린다.
        for (Thread thread : threads) {
            thread.join();
        }

        // 201개의 스레드를 시작했지만 200 응답을 받은 것은 200개 뿐이며, 1개는 대기 큐에 남아 있다.
        assertThat(count.intValue()).isEqualTo(maxThreads);
    }

    private static void incrementIfOk(HttpResponse<String> response) {
        if (response.statusCode() == 200) {
            count.incrementAndGet();
        }
    }
}
