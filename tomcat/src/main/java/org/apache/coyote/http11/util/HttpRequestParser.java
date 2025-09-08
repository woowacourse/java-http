package org.apache.coyote.http11.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.coyote.http11.dto.HttpRequest;
import org.apache.coyote.http11.dto.HttpRequestUri;

public final class HttpRequestParser {

    private static final int KEY_AND_VALUE_COUNT = 2;
    private static final String HTTP_HEADER_DELIMITER = ":";
    private static final String HTTP_URL_DELIMITER = " ";
    private static final String QUERY_STRING_DELIMITER = "&";
    private static final String QUERY_STRING_START_DELIMITER = "?";
    private static final String QUERY_KEY_VALUE_DELIMITER = "=";
    private static final int KEY_VALUE_PAIR_LENGTH = 2;
    private static final int QUERY_KEY_VALUE_LIMIT = 2;
    private static final int MAX_URI_SPLIT_COUNT = 3; // "GET /a?b=1 HTTP/1.1"

    private HttpRequestParser() {
    }

    public static HttpRequest parse(InputStream inputStream) throws IOException {
        Head head = readHead(inputStream);
        HttpRequestUri uri = parseUri(head.startLine);
        HttpHeaders headers = parseHeaders(head.headerLines);
        Map<String, String> queryParams = parseQueryString(uri.queryString());
        return new HttpRequest(uri.method(), uri.path(), uri.version(), headers, queryParams);
    }

    private static Head readHead(InputStream inputStream) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.ISO_8859_1));

        String start = br.readLine();
        if (start == null || start.isEmpty()) {
            throw new IOException("Empty request line");
        }

        List<String> headerLines = new ArrayList<>();
        String line;
        while ((line = br.readLine()) != null) {
            if (line.isEmpty()) {
                break;
            }
            headerLines.add(line);
        }
        return new Head(start, headerLines, br);
    }

    private static HttpRequestUri parseUri(String requestLine) {
        String[] parts = requestLine.split(HTTP_URL_DELIMITER, MAX_URI_SPLIT_COUNT);
        if (parts.length != MAX_URI_SPLIT_COUNT) {
            throw new IllegalArgumentException("잘못된 요청입니다. " + requestLine);
        }

        String method = parts[0];
        String uri = parts[1];
        String version = parts[2];

        int queryIndex = uri.indexOf(QUERY_STRING_START_DELIMITER);
        String path = (queryIndex < 0) ? uri : uri.substring(0, queryIndex);
        String query = (queryIndex < 0) ? "" : uri.substring(queryIndex + 1);
        return new HttpRequestUri(method, path, version, query);
    }

    private static HttpHeaders parseHeaders(List<String> lines) {
        HttpHeaders headers = new HttpHeaders();
        for (String line : lines) {
            parseHeaderLine(line).ifPresent(e -> headers.add(e.getKey(), e.getValue()));
        }
        return headers;
    }

    private static Optional<Map.Entry<String,String>> parseHeaderLine(String line) {
        String[] parts = line.split(HTTP_HEADER_DELIMITER, KEY_AND_VALUE_COUNT);
        if (parts.length != KEY_AND_VALUE_COUNT) return Optional.empty();
        return Optional.of(Map.entry(parts[0].strip(), parts[1].strip()));
    }

    private static Map<String, String> parseQueryString(String queryString) {
        if (queryString == null || queryString.isEmpty()) {
            return Map.of();
        }
        return Arrays.stream(queryString.split(QUERY_STRING_DELIMITER))
                .map(s -> s.split(QUERY_KEY_VALUE_DELIMITER, QUERY_KEY_VALUE_LIMIT))
                .filter(arr -> arr.length == KEY_VALUE_PAIR_LENGTH)
                .collect(Collectors.toMap(
                        arr -> decode(arr[0]),
                        arr -> decode(arr[1]),
                        (oldVal, newVal) -> newVal
                ));
    }

    private static String decode(String value) {
        try {
            return URLDecoder.decode(value, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            return value;
        }
    }

    private record Head(String startLine, List<String> headerLines, BufferedReader bodyReader) {}
}
