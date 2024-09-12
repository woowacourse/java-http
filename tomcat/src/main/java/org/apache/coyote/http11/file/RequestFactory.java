package org.apache.coyote.http11.file;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Objects;

public class RequestFactory {
    private static final String ERROR_MESSAGE = "파일 읽기를 실패했습니다.";
    private static final String HTTP_LINE_SEPARATOR = "\r\n";

    public static String readRequestLine(BufferedReader reader) throws IOException {
        return reader.readLine();
    }

    public static String readHeaders(BufferedReader reader) throws IOException {
        final StringBuilder requestHeaders = new StringBuilder();
        String line;
        while (Objects.nonNull(line = reader.readLine()) && !line.isBlank()) {
            requestHeaders.append(line).append(HTTP_LINE_SEPARATOR);
        }
        return requestHeaders.toString();
    }

    public static String readBody(BufferedReader reader, int contentLength) throws IOException {
        char[] buffer = new char[contentLength];
        int charsRead = reader.read(buffer, 0, contentLength);
        if (charsRead == -1) {
            throw new RuntimeException(ERROR_MESSAGE);
        }
        return new String(buffer);
    }
}
