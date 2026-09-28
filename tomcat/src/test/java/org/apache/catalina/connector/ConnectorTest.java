package org.apache.catalina.connector;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.coyote.ProcessorFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ConnectorTest {

    @Test
    @DisplayName("설정한 최대 스레드 수 안에서 연결을 처리하고 스레드를 재사용한다")
    void reusesWorkerWithinMaxThreads() throws IOException, InterruptedException {
        final int port = availablePort();
        final AtomicInteger accepted = new AtomicInteger();
        final AtomicReference<Thread> firstWorker = new AtomicReference<>();
        final AtomicReference<Thread> secondWorker = new AtomicReference<>();
        final CountDownLatch firstStarted = new CountDownLatch(1);
        final CountDownLatch secondAccepted = new CountDownLatch(1);
        final CountDownLatch releaseFirst = new CountDownLatch(1);
        final CountDownLatch finished = new CountDownLatch(2);

        final ProcessorFactory processorFactory = connection -> {
            final int requestNumber = accepted.incrementAndGet();
            if (requestNumber == 2) {
                secondAccepted.countDown();
            }
            return () -> {
                try (connection) {
                    if (requestNumber == 1) {
                        firstWorker.set(Thread.currentThread());
                        firstStarted.countDown();
                        releaseFirst.await();
                    } else {
                        secondWorker.set(Thread.currentThread());
                    }
                } catch (IOException e) {
                    throw new IllegalStateException(e);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    finished.countDown();
                }
            };
        };

        final Connector connector = new Connector(port, 100, 1, processorFactory);
        connector.start();
        try (Socket first = new Socket("127.0.0.1", port)) {
            assertThat(firstStarted.await(5, TimeUnit.SECONDS)).isTrue();
            try (Socket second = new Socket("127.0.0.1", port)) {
                assertThat(secondAccepted.await(5, TimeUnit.SECONDS)).isTrue();
                releaseFirst.countDown();
                assertThat(finished.await(5, TimeUnit.SECONDS)).isTrue();
                assertThat(secondWorker.get()).isSameAs(firstWorker.get());
            }
        } finally {
            releaseFirst.countDown();
            connector.stop();
        }

        final Thread worker = firstWorker.get();
        worker.join(TimeUnit.SECONDS.toMillis(5));
        assertThat(worker.isAlive()).isFalse();
    }

    private int availablePort() throws IOException {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        }
    }
}
