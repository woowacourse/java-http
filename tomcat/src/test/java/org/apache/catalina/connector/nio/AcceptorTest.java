package org.apache.catalina.connector.nio;

import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.function.Consumer;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AcceptorTest {

    @Test
    void 연결을_수락하고_논블로킹으로_전환한_뒤_Poller에_전달한다() throws Exception {
        final var serverChannel = mock(ServerSocketChannel.class);
        final var socketChannel = mock(SocketChannel.class);
        @SuppressWarnings("unchecked")
        final Consumer<SocketChannel> registrar = mock(Consumer.class);
        final var acceptor = new Acceptor(serverChannel, registrar);
        when(serverChannel.accept()).thenReturn(socketChannel);

        acceptor.acceptConnection();

        final InOrder inOrder = inOrder(serverChannel, socketChannel, registrar);
        inOrder.verify(serverChannel).accept();
        inOrder.verify(socketChannel).configureBlocking(false);
        inOrder.verify(registrar).accept(socketChannel);
    }
}
