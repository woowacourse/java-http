package org.apache.catalina.connector;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import org.apache.catalina.servlet.Servlet;
import org.apache.catalina.servlet.ServletManger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ConnectorTest {

    @DisplayName("스레드 크기가 2일 때, 요청이 4개가 들어오면 2개의 스레드만 사용하여 처리한다.")
    @Test
    void test() throws InterruptedException {
        // given
        final int port = 63300;
        final Connector connector = new Connector(
                port,
                250,
                new StubServletContainer(),
                2
        );
        connector.start();
        final var latch = new CountDownLatch(4);

        // when
        for (int i = 0; i < 4; i++) {
            final TestHttpUtils testHttpUtils = new TestHttpUtils(latch, port);
            final Thread thread = new Thread(() -> testHttpUtils.send("/index.html"));
            thread.start();
        }
        latch.await();

        // then
        final var completedTasks = StubServletContainer.map.values()
                                                           .stream()
                                                           .mapToInt(Integer::intValue)
                                                           .sum();
        assertAll(
                () -> assertThat(StubServletContainer.map).hasSize(2),
                () -> assertThat(completedTasks).isEqualTo(4)
        );
    }

    @DisplayName("스레드 크기가 1 이고, acceptCount가 1 이라면 나머지 요청은 수행되지 못한다.")
    @Test
    void acceptCountTest() throws InterruptedException {
        // given
        final int port = 63303;
        final var connector = new Connector(
                port,
                1,
                new StubServletContainerWaitForever(),
                1
        );
        connector.start();
        final var latch = new CountDownLatch(10);
        final var testHttpUtils2 = new TestHttpUtils(latch, port);

        // when
        for (int i = 0; i < 10; i++) {
            final var testHttpUtils = new TestHttpUtils(latch, port);
            final var thread = new Thread(() -> testHttpUtils.send("/index.html"));
            thread.start();
        }
        Thread.sleep(300);
        final var completedTasks = StubServletContainerWaitForever.map.values()
                                                                      .stream()
                                                                      .mapToInt(Integer::valueOf)
                                                                      .sum();

        // then
        assertAll(
                () -> assertThatThrownBy(() -> testHttpUtils2.send("/index.html")).isInstanceOf(RuntimeException.class),
                () -> assertThat(completedTasks).isEqualTo(1)
        );
    }

    public static class StubServletContainer implements ServletManger {

        public static ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();

        @Override
        public Servlet createServlet() {
            return (httpRequest, httpResponse) -> map.merge(Thread.currentThread().getName(), 1, Integer::sum);
        }
    }

    public static class StubServletContainerWaitForever implements ServletManger {

        public static ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();

        @Override
        public Servlet createServlet() {
            return (httpRequest, httpResponse) -> {
                map.merge(Thread.currentThread().getName(), 1, Integer::sum);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            };
        }
    }

    public static class TestHttpUtils {

        private final HttpClient httpClient;
        private final CountDownLatch latch;
        private final int port;

        public TestHttpUtils(CountDownLatch latch, int port) {
            this.httpClient = HttpClient.newBuilder()
                                        .version(HttpClient.Version.HTTP_1_1)
                                        .connectTimeout(Duration.ofSeconds(1))
                                        .build();
            this.latch = latch;
            this.port = port;
        }

        public void send(final String path) {
            try {
                final var request = HttpRequest.newBuilder()
                                               .header("Accept", "*/*")
                                               .uri(URI.create("http://localhost:" + port + path))
                                               .timeout(Duration.ofSeconds(1))
                                               .build();
                httpClient.send(request, BodyHandlers.ofString());
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                latch.countDown();
            }
        }
    }
}
