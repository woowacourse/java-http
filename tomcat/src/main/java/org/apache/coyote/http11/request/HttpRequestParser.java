package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class HttpRequestParser {

    private static final String HEADER_DELIMITER = ": ";

    private HttpRequestParser() {
    }

    public static HttpRequest extract(BufferedReader reader) throws IOException {
        RequestLine requestLine = extractRequestLine(reader);
        RequestHeaders requestHeaders = extractRequestHeaders(reader);
        RequestBody requestBody = extractRequestBody(reader, requestHeaders.contentLength());

        return new HttpRequest(requestLine, requestHeaders, requestBody);
    }

    private static RequestLine extractRequestLine(BufferedReader reader) throws IOException {
        String requestLine = reader.readLine();
        if (requestLine == null) {
            throw new IllegalArgumentException("잘못된 요청입니다.");
        }
        return RequestLine.from(requestLine);
    }

    private static RequestHeaders extractRequestHeaders(BufferedReader reader) throws IOException {
        Map<String, String> requestHeaders = new HashMap<>();
        String line = reader.readLine();

        while (canRead(line)) {
            mapRequestHeaders(requestHeaders, line);
            line = reader.readLine();
        }

        return RequestHeaders.from(requestHeaders);
    }

    private static boolean canRead(String line) {
        return !(line == null || line.isEmpty());
    }

    private static void mapRequestHeaders(Map<String, String> requestHeaders, String line) {
        String[] split = line.split(HEADER_DELIMITER, 2);

        if (split.length == 2) {
            requestHeaders.put(split[0], split[1]);
        }
    }

    private static RequestBody extractRequestBody(BufferedReader reader, String contentLength) throws IOException {
        int length = Optional.ofNullable(contentLength)
                .map(Integer::parseInt)
                .orElseThrow(() -> new IOException("contentLength를 확인해주세요."));

        if (isEmptyOrBufferNotReady(reader, length)) {
            return RequestBody.EMPTY;
        }

        char[] buffer = new char[length];
        reader.read(buffer, 0, length);

        return RequestBody.from(new String(buffer));
    }

    private static boolean isEmptyOrBufferNotReady(BufferedReader reader, int length) throws IOException {
        return length == 0 || !reader.ready();
    }
}
