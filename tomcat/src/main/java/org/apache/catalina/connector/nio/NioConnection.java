package org.apache.catalina.connector.nio;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

final class NioConnection {

    private static final int INITIAL_REQUEST_BUFFER_SIZE = 8 * 1024;
    private static final int MAX_REQUEST_SIZE = 1024 * 1024;
    private static final byte[] HEADER_END = "\r\n\r\n".getBytes(StandardCharsets.ISO_8859_1);

    private final SocketChannel channel;
    private ByteBuffer requestBuffer = ByteBuffer.allocate(INITIAL_REQUEST_BUFFER_SIZE);

    NioConnection(final SocketChannel channel) {
        this.channel = channel;
    }

    void append(final ByteBuffer source) {
        ensureCapacity(source.remaining());
        requestBuffer.put(source);
    }

    boolean isRequestComplete() {
        final byte[] request = requestBytes();
        final int headerEnd = findHeaderEnd(request);
        if (headerEnd < 0) {
            return false;
        }

        final int contentLength = findContentLength(request, headerEnd);
        return request.length >= headerEnd + contentLength;
    }

    byte[] requestBytes() {
        final ByteBuffer copy = requestBuffer.asReadOnlyBuffer();
        copy.flip();
        final byte[] request = new byte[copy.remaining()];
        copy.get(request);
        return request;
    }

    SocketChannel channel() {
        return channel;
    }

    private int findHeaderEnd(final byte[] request) {
        for (int i = 0; i <= request.length - HEADER_END.length; i++) {
            if (matchesHeaderEnd(request, i)) {
                return i + HEADER_END.length;
            }
        }
        return -1;
    }

    private boolean matchesHeaderEnd(final byte[] request, final int offset) {
        for (int i = 0; i < HEADER_END.length; i++) {
            if (request[offset + i] != HEADER_END[i]) {
                return false;
            }
        }
        return true;
    }

    private int findContentLength(final byte[] request, final int headerEnd) {
        final String headers = new String(request, 0, headerEnd, StandardCharsets.ISO_8859_1);
        return Arrays.stream(headers.split("\r\n"))
                .map(line -> line.split(":", 2))
                .filter(header -> header.length == 2)
                .filter(header -> header[0].trim().equalsIgnoreCase("Content-Length"))
                .map(header -> header[1].trim())
                .mapToInt(Integer::parseInt)
                .findFirst()
                .orElse(0);
    }

    private void ensureCapacity(final int additionalBytes) {
        final int requiredCapacity = requestBuffer.position() + additionalBytes;
        if (requiredCapacity > MAX_REQUEST_SIZE) {
            throw new IllegalArgumentException("HTTP 요청 크기는 1MB를 초과할 수 없습니다.");
        }
        if (requiredCapacity <= requestBuffer.capacity()) {
            return;
        }

        final int newCapacity = Math.min(
                Math.max(requestBuffer.capacity() * 2, requiredCapacity),
                MAX_REQUEST_SIZE
        );
        final ByteBuffer expanded = ByteBuffer.allocate(newCapacity);
        requestBuffer.flip();
        expanded.put(requestBuffer);
        requestBuffer = expanded;
    }
}
