package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public final class HttpRequest {
    private static final String CONTENT_LENGTH_HEADER = "content-length";

    private final RequestLine requestLine;
    private final Map<String, String> headers;
    private final String body;

    private HttpRequest(RequestLine requestLine, Map<String, String> headers, String body) {
        this.requestLine = requestLine;
        this.headers = Map.copyOf(headers);
        this.body = body;
    }

    public static HttpRequest from(InputStream input) throws IOException {
        BufferedReader reader = createReader(input);
        RequestLine requestLine = readRequestLine(reader);
        Map<String, String> headers = readHeaders(reader);
        String body = readBody(reader, headers);
        return new HttpRequest(requestLine, headers, body);
    }

    private static BufferedReader createReader(InputStream input) {
        return new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8));
    }

    private static RequestLine readRequestLine(BufferedReader reader) throws IOException {
        return RequestLine.from(reader.readLine());
    }

    private static Map<String, String> readHeaders(BufferedReader reader) throws IOException {
        Map<String, String> headers = new HashMap<>();
        String line;
        while ((line = reader.readLine()) != null && !line.isBlank()) {
            addHeader(headers, line);
        }
        return headers;
    }

    private static void addHeader(Map<String, String> headers, String line) {
        int separator = line.indexOf(':');
        if (separator <= 0) {
            return;
        }
        String name = normalizeHeaderName(line.substring(0, separator));
        String value = line.substring(separator + 1).trim();
        headers.put(name, value);
    }

    private static String normalizeHeaderName(String name) {
        return name.trim().toLowerCase(Locale.ROOT);
    }

    private static String readBody(BufferedReader reader, Map<String, String> headers) throws IOException {
        int contentLength = contentLength(headers);
        if (contentLength == 0) {
            return "";
        }
        return readCharacters(reader, contentLength);
    }

    private static int contentLength(Map<String, String> headers) {
        return Integer.parseInt(headers.getOrDefault(CONTENT_LENGTH_HEADER, "0"));
    }

    private static String readCharacters(BufferedReader reader, int length) throws IOException {
        char[] characters = new char[length];
        int readLength = 0;
        while (readLength < length) {
            int read = reader.read(characters, readLength, length - readLength);
            if (read < 0) {
                break;
            }
            readLength += read;
        }
        return new String(characters, 0, readLength);
    }

    public RequestLine requestLine() {
        return requestLine;
    }

    public Map<String, String> headers() {
        return headers;
    }

    public Optional<String> header(String name) {
        return Optional.ofNullable(headers.get(normalizeHeaderName(name)));
    }

    public String body() {
        return body;
    }
}
