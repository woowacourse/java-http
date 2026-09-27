package org.apache.coyote.http11.request;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

final class HttpInput {

    private HttpInput() {
    }

    static String readLine(final InputStream inputStream) throws IOException {
        final ByteArrayOutputStream line = new ByteArrayOutputStream();
        int current;

        while ((current = inputStream.read()) != -1) {
            if (current == '\n') {
                break;
            }
            line.write(current);
        }

        if (current == -1 && line.size() == 0) {
            return null;
        }

        final byte[] bytes = line.toByteArray();
        final int length = bytes.length > 0 && bytes[bytes.length - 1] == '\r'
                ? bytes.length - 1
                : bytes.length;
        return new String(bytes, 0, length, StandardCharsets.ISO_8859_1);
    }
}
