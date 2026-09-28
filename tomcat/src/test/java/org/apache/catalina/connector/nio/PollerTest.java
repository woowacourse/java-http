package org.apache.catalina.connector.nio;

import org.junit.jupiter.api.Test;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PollerTest {

    @Test
    void 새_채널을_읽기_이벤트와_NioConnection으로_등록한다() throws Exception {
        final var selector = mock(Selector.class);
        final var channel = mock(SocketChannel.class);
        final var poller = new Poller(selector, Runnable::run, connection -> { });

        poller.register(channel);
        poller.registerPendingConnections();

        verify(selector).wakeup();
        verify(channel).register(
                eq(selector),
                eq(SelectionKey.OP_READ),
                any(NioConnection.class)
        );
    }

    @Test
    void 요청이_완성되면_키를_취소하고_Executor에서_처리한다() throws Exception {
        final var selector = mock(Selector.class);
        final var key = mock(SelectionKey.class);
        final var connection = mock(NioConnection.class);
        @SuppressWarnings("unchecked")
        final Consumer<NioConnection> processor = mock(Consumer.class);
        final Executor directExecutor = Runnable::run;
        final var poller = new Poller(selector, directExecutor, processor);
        when(key.isReadable()).thenReturn(true);
        when(key.attachment()).thenReturn(connection);
        when(connection.read()).thenReturn(10);
        when(connection.isRequestComplete()).thenReturn(true);

        poller.processKey(key);

        verify(key).cancel();
        verify(processor).accept(connection);
    }

    @Test
    void 클라이언트가_연결을_종료하면_키와_채널을_정리한다() throws Exception {
        final var selector = mock(Selector.class);
        final var key = mock(SelectionKey.class);
        final var connection = mock(NioConnection.class);
        final var poller = new Poller(selector, Runnable::run, ignored -> { });
        when(key.isReadable()).thenReturn(true);
        when(key.attachment()).thenReturn(connection);
        when(connection.read()).thenReturn(-1);

        poller.processKey(key);

        verify(key).cancel();
        verify(connection).close();
    }
}
