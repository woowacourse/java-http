package org.apache.coyote.http11;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public final class HttpParser {

    private static final int MAX_LINE_LENGTH = 8192;

    private HttpParser() {
    }

    public static String readLine(final InputStream inputStream) throws IOException {
        final var buffer = new ByteArrayOutputStream();
        int nextByte;
        while ((nextByte = inputStream.read()) != -1) {
            if (buffer.size() >= MAX_LINE_LENGTH) {
                throw new IOException("요청 라인/헤더가 최대 길이 " + MAX_LINE_LENGTH + "를 초과합니다.");
            }
            if (nextByte == '\n') {
                break;
            }
            if (nextByte == '\r') {
                inputStream.read();
                break;
            }
            buffer.write(nextByte);
        }
        return buffer.toString(StandardCharsets.US_ASCII);
    }
}
