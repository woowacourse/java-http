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

    @Test
    @DisplayName("작업 대기열이 가득 차면 연결을 닫고 이후 연결은 계속 처리한다")
    void rejectsConnectionWhenQueueIsFull() throws IOException, InterruptedException {
        final int port = availablePort();
        final AtomicInteger created = new AtomicInteger();
        final AtomicInteger processed = new AtomicInteger();
        final CountDownLatch firstStarted = new CountDownLatch(1);
        final CountDownLatch releaseFirst = new CountDownLatch(1);
        final CountDownLatch secondFinished = new CountDownLatch(1);
        final CountDownLatch fourthFinished = new CountDownLatch(1);

        final ProcessorFactory processorFactory = connection -> {
            final int requestNumber = created.incrementAndGet();
            return () -> {
                try (connection) {
                    if (requestNumber == 1) {
                        firstStarted.countDown();
                        releaseFirst.await();
                    }
                    processed.incrementAndGet();
                    if (requestNumber == 2) {
                        secondFinished.countDown();
                    }
                    if (requestNumber == 4) {
                        fourthFinished.countDown();
                    }
                } catch (IOException e) {
                    throw new IllegalStateException(e);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            };
        };

        final Connector connector = new Connector(port, 100, 1, 1, processorFactory);
        connector.start();
        try (Socket first = new Socket("127.0.0.1", port)) {
            assertThat(firstStarted.await(5, TimeUnit.SECONDS)).isTrue();
            try (Socket second = new Socket("127.0.0.1", port);
                 Socket third = new Socket("127.0.0.1", port)) {
                third.setSoTimeout((int) TimeUnit.SECONDS.toMillis(5));
                assertThat(third.getInputStream().read()).isEqualTo(-1);
                assertThat(created.get()).isEqualTo(3);

                releaseFirst.countDown();
                assertThat(secondFinished.await(5, TimeUnit.SECONDS)).isTrue();
                try (Socket fourth = new Socket("127.0.0.1", port)) {
                    assertThat(fourthFinished.await(5, TimeUnit.SECONDS)).isTrue();
                }
                assertThat(processed.get()).isEqualTo(3);
            }
        } finally {
            releaseFirst.countDown();
            connector.stop();
        }
    }

    private int availablePort() throws IOException {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        }
    }
}
