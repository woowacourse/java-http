package org.apache.coyote.http11.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Objects;

import org.apache.coyote.http11.request.HttpRequest;

public class RequestFactory {
    private static final String ERROR_MESSAGE = "파일 읽기를 실패했습니다.";
    private static final String HTTP_LINE_SEPARATOR = "\r\n";
    private static final String STATIC_PATH = "static";

    public static String readRequestLine(BufferedReader reader) throws IOException {
        return reader.readLine();
    }

    public static String readRequestHeaders(BufferedReader reader) throws IOException {
        final StringBuilder requestHeaders = new StringBuilder();
        String line;
        while (Objects.nonNull(line = reader.readLine()) && !line.isBlank()) {
            requestHeaders.append(line).append(HTTP_LINE_SEPARATOR);
        }
        return requestHeaders.toString();
    }

    public static String readRequestBody(BufferedReader reader, int contentLength) throws IOException {
        char[] buffer = new char[contentLength];
        int charsRead = reader.read(buffer, 0, contentLength);
        if (charsRead == -1) {
            throw new RuntimeException(ERROR_MESSAGE);
        }
        return new String(buffer);
    }

    public static String getResponseBody(HttpRequest request) throws IOException {
        String requestUri = request.getRequestUri();
        ClassLoader classLoader = RequestFactory.class.getClassLoader();
        URL resourceUrl = classLoader.getResource(STATIC_PATH + requestUri);
        if (Objects.isNull(resourceUrl)) {
            return "";
        }

        File file = new File(resourceUrl.getFile());
        return new String(Files.readAllBytes(file.toPath()));
    }
}
