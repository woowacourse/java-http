package org.apache.coyote.http11;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public final class Http11InputBuffer implements AutoCloseable {

    private final InputStream inputStream;

    private final byte[] buffer;
    private int position = 0;
    private int limit = 0;

    private static final int DEFAULT_BUF = 8192;

    public Http11InputBuffer(InputStream inputStream) {
        this(inputStream, DEFAULT_BUF);
    }

    public Http11InputBuffer(InputStream inputStream, int bufferSize) {
        this.inputStream = inputStream;
        this.buffer = new byte[bufferSize];
    }

    public String readLine() throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(128);
        int lineLength = 0;

        while (true) {
            if (position >= limit) {
                if (!fill()) {
                    if (lineLength == 0) {
                        return null;
                    }
                    return decodeLine(byteArrayOutputStream);
                }
            }
            byte b = buffer[position++];
            if (b == (byte) '\n') {
                return decodeLine(byteArrayOutputStream);
            } else {
                byteArrayOutputStream.write(b);
                lineLength++;
            }
        }
    }

    private String decodeLine(ByteArrayOutputStream byteArrayOutputStream) {
        byte[] arr = byteArrayOutputStream.toByteArray();
        int len = arr.length;
        if (len > 0 && arr[len - 1] == (byte) '\r') {
            len--;
        }
        return new String(arr, 0, len, StandardCharsets.ISO_8859_1);
    }

    public byte[] readBytes(int length) throws IOException {
        byte[] out = new byte[length];
        int offset = 0;

        int remainingInBuffer = remainingInBuffer();
        if (remainingInBuffer > 0) {
            int take = Math.min(remainingInBuffer, length);
            System.arraycopy(buffer, position, out, 0, take);
            position += take;
            offset += take;
        }

        while (offset < length) {
            int r = inputStream.read(out, offset, length - offset);
            offset += r;
        }
        return out;
    }

    private int remainingInBuffer() {
        return Math.max(0, limit - position);
    }

    private boolean fill() throws IOException {
        position = 0;
        limit = inputStream.read(buffer);
        return limit > 0;
    }

    @Override
    public void close() throws IOException {
        inputStream.close();
    }
}
