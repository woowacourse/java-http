package org.apache.catalina.connector.nio;

import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class NioConnectionTest {

    @Test
    void 헤더가_나누어_도착하면_빈_줄까지_기다린다() {
        final var connection = new NioConnection(mock(SocketChannel.class));

        connection.append(buffer("GET /index.html HTTP/1.1\r\nHost: localhost\r\n"));

        assertThat(connection.isRequestComplete()).isFalse();

        connection.append(buffer("\r\n"));

        assertThat(connection.isRequestComplete()).isTrue();
    }

    @Test
    void Content_Length가_있으면_요청_본문까지_기다린다() {
        final var connection = new NioConnection(mock(SocketChannel.class));

        connection.append(buffer("POST /login HTTP/1.1\r\nContent-Length: 12\r\n\r\naccount="));

        assertThat(connection.isRequestComplete()).isFalse();

        connection.append(buffer("gugu"));

        assertThat(connection.isRequestComplete()).isTrue();
    }

    @Test
    void 나누어_도착한_요청을_하나의_바이트_배열로_반환한다() {
        final var connection = new NioConnection(mock(SocketChannel.class));
        final var first = "GET /index.html HTTP/1.1\r\n";
        final var second = "Host: localhost\r\n\r\n";

        connection.append(buffer(first));
        connection.append(buffer(second));

        assertThat(new String(connection.requestBytes(), StandardCharsets.ISO_8859_1))
                .isEqualTo(first + second);
    }

    private ByteBuffer buffer(final String value) {
        return ByteBuffer.wrap(value.getBytes(StandardCharsets.ISO_8859_1));
    }
}
