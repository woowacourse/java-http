package org.apache.catalina.connector;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class ConnectorTest {

    @Test
    void 포트가_유효한_범위를_벗어나면_예외가_발생한다() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new Connector(65536, 100, 250));
    }

    @Test
    void acceptCount가_0_이하면_예외가_발생한다() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new Connector(0, 0, 250));
    }

    @Test
    void maxThreads가_0_이하면_예외가_발생한다() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new Connector(0, 100, 0));
    }

    @Test
    void 스레드_수와_작업_큐_크기를_제한한다() throws Exception {
        final var connector = new Connector(0, 100, 2, 3);

        try {
            final var executor = getExecutor(connector);

            assertThat(executor.getCorePoolSize()).isEqualTo(2);
            assertThat(executor.getMaximumPoolSize()).isEqualTo(2);
            assertThat(executor.getQueue().remainingCapacity()).isEqualTo(3);
            assertThat(executor.getRejectedExecutionHandler())
                    .isInstanceOf(ThreadPoolExecutor.AbortPolicy.class);
        } finally {
            connector.stop();
        }
    }

    @Test
    void 스레드와_작업_큐가_가득_차면_요청의_소켓을_닫는다() throws Exception {
        final var connector = new Connector(0, 100, 1, 1);
        final var executor = getExecutor(connector);
        final var blocker = new CountDownLatch(1);
        final var connection = mock(Socket.class);

        try {
            executor.execute(() -> await(blocker));
            executor.execute(() -> { });

            connector.process(connection);

            verify(connection).close();
        } finally {
            blocker.countDown();
            connector.stop();
        }
    }

    @Test
    void maxQueueSize가_0_이하면_예외가_발생한다() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new Connector(0, 100, 250, 0));
    }

    private ThreadPoolExecutor getExecutor(final Connector connector) throws Exception {
        final Field field = Connector.class.getDeclaredField("executorService");
        field.setAccessible(true);
        return (ThreadPoolExecutor) field.get(connector);
    }

    private void await(final CountDownLatch blocker) {
        try {
            blocker.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
