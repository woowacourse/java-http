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
            final Connector connector = new Connector(8080, 100, 2,
                    new RequestMapping(Map.of("/", controller), controller));
            try {
                connector.start();
                assertThat(accepted.await(5, TimeUnit.SECONDS)).isTrue();
                assertThat(firstTwoStarted.await(5, TimeUnit.SECONDS)).isTrue();
                assertThat(thirdStarted.await(200, TimeUnit.MILLISECONDS)).isFalse();

                connector.stop();
                releaseWorkers.countDown();
                assertThat(thirdStarted.await(5, TimeUnit.SECONDS)).isTrue();
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
}
