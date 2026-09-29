package org.apache.catalina.connector;

import org.apache.catalina.controller.Controller;
import org.apache.catalina.controller.RequestMapping;
import org.junit.jupiter.api.Test;
import support.StubSocket;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.when;

class ConnectorTest {

    @Test
    void limitsWorkersAndFinishesQueuedRequestsOnShutdown() throws Exception {
        final var connections = List.of(new StubSocket(), new StubSocket(), new StubSocket());
        final CountDownLatch accepted = new CountDownLatch(3);
        final CountDownLatch listeningSocketClosed = new CountDownLatch(1);
        final CountDownLatch firstTwoStarted = new CountDownLatch(2);
        final CountDownLatch thirdStarted = new CountDownLatch(1);
        final CountDownLatch releaseWorkers = new CountDownLatch(1);
        final CountDownLatch thirdTaskQueued = new CountDownLatch(1);
        final ObservedThreadPoolExecutor executor = new ObservedThreadPoolExecutor(thirdTaskQueued);
        final var workers = ConcurrentHashMap.<Thread>newKeySet();
        final AtomicInteger started = new AtomicInteger();
        final AtomicInteger nextConnection = new AtomicInteger();

        final Controller controller = (request, response) -> {
            workers.add(Thread.currentThread());
            if (started.incrementAndGet() <= 2) {
                firstTwoStarted.countDown();
            } else {
                thirdStarted.countDown();
            }
            if (!releaseWorkers.await(5, TimeUnit.SECONDS)) {
                throw new IllegalStateException("Workers were not released");
            }
        };

        try (var ignored = mockConstruction(ServerSocket.class, (socket, context) -> {
            when(socket.accept()).thenAnswer(invocation -> {
                final int index = nextConnection.getAndIncrement();
                if (index < connections.size()) {
                    accepted.countDown();
                    return connections.get(index);
                }
                listeningSocketClosed.await(5, TimeUnit.SECONDS);
                throw new IOException("Listening socket closed");
            });
            doAnswer(invocation -> {
                listeningSocketClosed.countDown();
                return null;
            }).when(socket).close();
        })) {
            final Connector connector = new Connector(8080, 100,
                    new RequestMapping(Map.of("/", controller), controller), executor);
            try {
                connector.start();
                assertThat(accepted.await(5, TimeUnit.SECONDS)).isTrue();
                assertThat(firstTwoStarted.await(5, TimeUnit.SECONDS)).isTrue();
                assertThat(thirdTaskQueued.await(5, TimeUnit.SECONDS)).isTrue();
                assertThat(executor.getActiveCount()).isEqualTo(2);
                assertThat(executor.getQueue()).hasSize(1);

                final Thread stopThread = new Thread(connector::stop);
                stopThread.start();
                assertThat(listeningSocketClosed.await(5, TimeUnit.SECONDS)).isTrue();
                assertThat(executor.awaitingTermination.await(5, TimeUnit.SECONDS)).isTrue();

                releaseWorkers.countDown();
                assertThat(thirdStarted.await(5, TimeUnit.SECONDS)).isTrue();
                stopThread.join(5000);
                assertThat(stopThread.isAlive()).isFalse();
                for (final Thread worker : workers) {
                    worker.join(5000);
                    assertThat(worker.isAlive()).isFalse();
                }

                assertThat(workers).hasSize(2);
                assertThat(connections).allSatisfy(connection -> {
                    assertThat(connection.isClosed()).isTrue();
                    assertThat(connection.output()).startsWith("HTTP/1.1 200");
                });
            } finally {
                releaseWorkers.countDown();
                connector.stop();
            }
        }
    }

    private static class ObservedThreadPoolExecutor extends ThreadPoolExecutor {

        private final CountDownLatch thirdTaskQueued;
        private final CountDownLatch awaitingTermination = new CountDownLatch(1);

        private ObservedThreadPoolExecutor(final CountDownLatch thirdTaskQueued) {
            super(2, 2, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
            this.thirdTaskQueued = thirdTaskQueued;
        }

        @Override
        public void execute(final Runnable command) {
            super.execute(command);
            if (getQueue().size() == 1) {
                thirdTaskQueued.countDown();
            }
        }

        @Override
        public boolean awaitTermination(final long timeout, final TimeUnit unit) throws InterruptedException {
            awaitingTermination.countDown();
            return super.awaitTermination(timeout, unit);
        }
    }
}
