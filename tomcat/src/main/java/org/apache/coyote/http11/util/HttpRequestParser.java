package org.apache.coyote.http11.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.coyote.http11.dto.HttpRequest;
import org.apache.coyote.http11.dto.HttpRequestUrl;

public final class HttpRequestParser {

    private static final int KEY_AND_VALUE_COUNT = 2;
    private static final String HTTP_HEADER_DELIMITER = ":";
    private static final String HTTP_URL_DELIMITER = " ";
    private static final String QUERY_STRING_DELIMITER = "&";
    private static final String QUERY_STRING_START_DELIMITER = "?";
    private static final String QUERY_KEY_VALUE_DELIMITER = "=";
    private static final int KEY_VALUE_PAIR_LENGTH = 2;
    private static final int QUERY_KEY_VALUE_LIMIT = 2;

    private HttpRequestParser() {
    }

    public static HttpRequest parse(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        HttpRequestUrl httpRequestUrl = readUri(reader);
        HttpHeaders headers = readHeaders(reader);
        Map<String, String> queryParams = parseQueryString(httpRequestUrl.queryString());
        return new HttpRequest(httpRequestUrl.method(), httpRequestUrl.path(), httpRequestUrl.version(), headers, queryParams);
    }

    private static HttpRequestUrl readUri(BufferedReader reader) throws IOException {
        String requestLine = reader.readLine();
        String[] parts = requestLine.split(HTTP_URL_DELIMITER);

        String method = parts[0];
        String uri = parts[1];
        String version = parts[2];

        int queryStartIndex = uri.indexOf(HttpRequestParser.QUERY_STRING_START_DELIMITER);
        String path = (queryStartIndex == -1) ? uri : uri.substring(0, queryStartIndex);
        String queryString = (queryStartIndex == -1) ? "" : uri.substring(queryStartIndex + 1);

        return new HttpRequestUrl(method, path, version, queryString);
    }

    private static HttpHeaders readHeaders(BufferedReader reader) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        String line;
        while (hasMoreHeader(line = reader.readLine())) {
            parseHeaderLine(line).ifPresent(e -> headers.add(e.getKey(), e.getValue()));
        }
        return headers;
    }

    private static boolean hasMoreHeader(String line) {
        return line != null && !line.isEmpty();
    }

    private static Optional<Entry<String, String>> parseHeaderLine(String line) {
        String[] parts = line.split(HTTP_HEADER_DELIMITER, KEY_AND_VALUE_COUNT);
        if (parts.length != KEY_AND_VALUE_COUNT) {
            return Optional.empty();
        }
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
}
