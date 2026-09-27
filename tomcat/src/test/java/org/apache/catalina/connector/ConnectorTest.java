package org.apache.catalina.connector;

import org.apache.catalina.Container;
import org.apache.catalina.controller.Controller;
import org.apache.catalina.controller.RequestMapping;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.MimeType;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@DisplayName("커넥터")
@Timeout(20)
class ConnectorTest {
    private static final int ACCEPT_COUNT = 100;
    private static final int MAX_THREADS = 2;
    private static final int REQUEST_COUNT = 5;
    private static final String BLOCKING_PATH = "/blocking";
    private static final Duration TIMEOUT = Duration.ofSeconds(5);
    // 초과 요청이 있다면 처리 스레드에 도달하기에 충분한 시간
    private static final Duration GRACE_PERIOD = Duration.ofMillis(500);

    private BlockingController controller;
    private Connector connector;
    private ExecutorService clients;
    private int port;

    @BeforeEach
    void setUp() throws IOException {
        controller = new BlockingController();
        final RequestMapping requestMapping = new RequestMapping();
        requestMapping.register(BLOCKING_PATH, controller);

        port = findFreePort();
        final Container container = new Container(new SessionManager(), requestMapping);
        connector = new Connector(container, port, ACCEPT_COUNT, MAX_THREADS);
        connector.start();
        clients = Executors.newFixedThreadPool(REQUEST_COUNT);
    }

    private static int findFreePort() throws IOException {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        }
    }

    @AfterEach
    void tearDown() {
        controller.release();
        connector.stop();
        clients.shutdownNow();
    }

    @Test
    @DisplayName("요청이 몰려도 maxThreads개까지만 동시에 처리한다")
    void neverExceedMaxThreads() throws Exception {
        // when
        sendConcurrently(REQUEST_COUNT);
        awaitUntil(() -> controller.inFlight() >= MAX_THREADS);
        Thread.sleep(GRACE_PERIOD.toMillis());

        // then
        assertThat(controller.maxInFlight()).isEqualTo(MAX_THREADS);
    }

    private List<Future<String>> sendConcurrently(final int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> clients.submit(() -> send(BLOCKING_PATH)))
                .toList();
    }

    private String send(final String path) throws IOException {
        try (Socket socket = new Socket(InetAddress.getLoopbackAddress(), port)) {
            socket.setSoTimeout((int) TIMEOUT.toMillis());

            final OutputStream outputStream = socket.getOutputStream();
            final String request = String.join("\r\n",
                    "GET " + path + " HTTP/1.1",
                    "Host: localhost:" + port,
                    "",
                    "");
            outputStream.write(request.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();

            return new String(socket.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private void awaitUntil(final BooleanSupplier condition) throws InterruptedException {
        final long deadline = System.nanoTime() + TIMEOUT.toNanos();
        while (!condition.getAsBoolean()) {
            if (System.nanoTime() > deadline) {
                fail("제한 시간 안에 조건을 만족하지 못했습니다.");
            }
            Thread.sleep(10);
        }
    }

    @Test
    @DisplayName("스레드가 모두 사용 중일 때 들어온 요청은 대기했다가 처리된다")
    void processWaitingRequestsWhenThreadsBecomeAvailable() throws Exception {
        // given
        final List<Future<String>> responses = sendConcurrently(REQUEST_COUNT);
        awaitUntil(() -> controller.inFlight() >= MAX_THREADS);

        // when
        controller.release();

        // then
        for (final Future<String> response : responses) {
            assertThat(response.get(TIMEOUT.toMillis(), TimeUnit.MILLISECONDS)).startsWith("HTTP/1.1 200 OK");
        }
    }

    @Test
    @DisplayName("요청마다 스레드를 새로 만들지 않고 재사용한다")
    void reuseThreads() throws Exception {
        // given
        controller.release();

        // when
        for (int i = 0; i < REQUEST_COUNT; i++) {
            send(BLOCKING_PATH);
        }

        // then
        assertThat(controller.handlerThreads()).hasSizeLessThanOrEqualTo(MAX_THREADS);
    }

    @Test
    @DisplayName("커넥터를 멈추면 요청을 처리하던 스레드도 종료된다")
    void terminateHandlerThreadsOnStop() throws Exception {
        // given
        controller.release();
        send(BLOCKING_PATH);
        final Set<Thread> handlerThreads = controller.handlerThreads();

        // when
        connector.stop();

        // then
        assertThat(handlerThreads).isNotEmpty();
        for (final Thread thread : handlerThreads) {
            thread.join(TIMEOUT.toMillis());
            assertThat(thread.isAlive()).isFalse();
        }
    }

    /**
     * release()가 호출될 때까지 요청을 붙잡아 두면서, 동시에 몇 개를 처리하는지와 어떤 스레드가 처리했는지 기록한다.
     */
    private static class BlockingController implements Controller {
        private static final byte[] BODY = "done".getBytes(StandardCharsets.UTF_8);

        private final CountDownLatch gate = new CountDownLatch(1);
        private final AtomicInteger inFlight = new AtomicInteger();
        private final AtomicInteger maxInFlight = new AtomicInteger();
        private final Set<Thread> handlerThreads = ConcurrentHashMap.newKeySet();

        @Override
        public void service(final HttpRequest request, final HttpResponse response) throws InterruptedException {
            handlerThreads.add(Thread.currentThread());
            maxInFlight.accumulateAndGet(inFlight.incrementAndGet(), Math::max);
            try {
                gate.await(TIMEOUT.toMillis(), TimeUnit.MILLISECONDS);
            } finally {
                inFlight.decrementAndGet();
            }
            response.setBody(MimeType.TEXT_HTML, BODY);
        }

        void release() {
            gate.countDown();
        }

        int inFlight() {
            return inFlight.get();
        }

        int maxInFlight() {
            return maxInFlight.get();
        }

        Set<Thread> handlerThreads() {
            return Set.copyOf(handlerThreads);
        }
    }
}
