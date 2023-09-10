package org.apache.catalina.connector;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import nextstep.servlet.Servlet;
import nextstep.servlet.ServletContainer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ConnectorTest {

    @DisplayName("스레드 크기가 5일 때, 요청이 5개가 들어오면 정상적으로 실행된다.")
    @Order(1)
    @Test
    void processAll() throws InterruptedException {
        // given
        int port = makeRandomPort();
        final AtomicInteger threads = new AtomicInteger(0);
        final Connector connector = new Connector(new StubServletContainer(), port, 250, 4);
        connector.start();

        // when
        for (int i = 0; i < 4; i++) {
            final Thread thread = new Thread(() -> new TestHttpUtils(port).send("/index.html", threads));
            thread.start();
        }

        Thread.sleep(1000);
        assertThat(threads.get()).isEqualTo(0);
    }

    @DisplayName("스레드 크기가 5일 때, 요청이 10개가 들어오면 5개가 대기한다.")
    @Order(2)
    @Test
    void waitFiveThreads() throws InterruptedException {
        // given
        int port = makeRandomPort();
        final AtomicInteger threads = new AtomicInteger(0);
        final Connector connector = new Connector(new StubServletContainer(), port, 250, 1);
        connector.start();

        // when
        for (int i = 0; i < 4; i++) {
            new Thread(() -> new TestHttpUtils(port).send("/index.html", threads)).start();
        }

        Thread.sleep(1000);
        assertThat(threads.get()).isEqualTo(3);
    }

    private int makeRandomPort() {
        return new Random().nextInt(60000) + 1;
    }

    public class StubServletContainer implements ServletContainer {

        @Override
        public Servlet createServlet() {
            return new StubServlet();
        }

        public class StubServlet implements Servlet {

            @Override
            public void service(final org.apache.coyote.http11.HttpRequest httpRequest,
                final org.apache.coyote.http11.HttpResponse httpResponse) throws IOException {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public class TestHttpUtils {

        private final int port;

        public TestHttpUtils(final int port) {
            this.port = port;
        }

        private final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .connectTimeout(Duration.ofSeconds(1))
            .build();

        public void send(final String path, final AtomicInteger waitingThreads) {
            final var request = HttpRequest.newBuilder()
                .header("Accept", "*/*")
                .uri(URI.create("http://localhost:" + port + path))
                .timeout(Duration.ofSeconds(3))
                .build();

            final long beforeSend = System.currentTimeMillis();

            try {
                httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }

            final long afterSend = System.currentTimeMillis();

            if (afterSend - beforeSend > 200) {
                waitingThreads.incrementAndGet();
            }
        }
    }
}
