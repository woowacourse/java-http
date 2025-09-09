package org.apache.coyote.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.constant.RequestLine;

public class StreamReader {

    public static String readFile(InputStream inputStream) throws IOException {
        Objects.requireNonNull(inputStream);
        try (final BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }

    public static HttpRequest readRequest(InputStream inputStream) throws IOException {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        final RequestLine requestLine = readRequestLine(reader);
        if (requestLine == null) {
            return null;
        }
        final Map<String, String> header = readHeader(reader);
        if (!header.containsKey("Content-Length")) {
            return new HttpRequest(requestLine, header, null);
        }
        int contentLength = Integer.parseInt(header.get("Content-Length"));
        if (contentLength == 0) {
            return new HttpRequest(requestLine, header, null);
        }
        final String body = readBody(reader, contentLength);
        return new HttpRequest(requestLine, header, body);
    }

    private static RequestLine readRequestLine(BufferedReader reader) throws IOException {
        final String line = reader.readLine();
        if (line == null || line.trim().isEmpty()) {
            return null;
        }
        return RequestLine.from(line);
    }

    private static Map<String, String> readHeader(BufferedReader reader) throws IOException {
        final Map<String, String> headers = new HashMap<>();
        String line;
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            String[] parts = line.split(":", 2);
            if (parts.length == 2) {
                headers.put(parts[0].trim(), parts[1].trim());
            }
        }
        return headers;
    }

    private static String readBody(BufferedReader reader, int contentLength) throws IOException {
        if (contentLength <= 0) {
            return null;
        }
        final char[] buffer = new char[contentLength];
        int totalRead = 0;
        while (totalRead < contentLength) {
            final int charsRead = reader.read(buffer, totalRead, contentLength - totalRead);
            if (charsRead == -1) {
                break;
            }
            totalRead += charsRead;
        }
        if (totalRead == 0) {
            return null;
        }
        return new String(buffer, 0, totalRead);
    }
}
