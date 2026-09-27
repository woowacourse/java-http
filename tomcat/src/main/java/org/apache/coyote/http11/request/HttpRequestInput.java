package org.apache.coyote.http11.request;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class HttpRequestInput {

    private final InputStream inputStream;

    public HttpRequestInput(final InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public String readLine() throws IOException {
        final ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        int current;

        while ((current = inputStream.read()) != -1) {
            if (current == '\r') {
                if (handleCarriageReturn(buffer)) {
                    return toString(buffer);
                }

                continue;
            }

            buffer.write(current);
        }

        if (buffer.size() == 0) {
            return null;
        }

        return toString(buffer);
    }

    public byte[] readBytes(final int length) throws IOException {
        validateLength(length);
        final byte[] bytes = new byte[length];
        readFully(bytes);
        return bytes;
    }

    private boolean handleCarriageReturn(
            final ByteArrayOutputStream buffer
    ) throws IOException {
        final int next = inputStream.read();

        if (next == '\n') {
            return true;
        }

        buffer.write('\r');

        if (next != -1) {
            buffer.write(next);
        }

        return false;
    }

    private void readFully(
            final byte[] bytes
    ) throws IOException {
        int offset = 0;

        while (offset < bytes.length) {
            final int read = inputStream.read(
                    bytes,
                    offset,
                    bytes.length - offset
            );

            if (read == -1) {
                throw new IllegalArgumentException("요청한 길이보다 데이터가 짧습니다.");
            }

            offset += read;
        }
    }

    private void validateLength(final int length) {
        if (length < 0) {
            throw new IllegalArgumentException("읽을 byte 길이는 음수일 수 없습니다.");
        }
    }

    private String toString(final ByteArrayOutputStream buffer) {
        return new String(
                buffer.toByteArray(),
                StandardCharsets.UTF_8
        );
    }
}
