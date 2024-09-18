package org.apache.coyote.http11;

import static org.apache.coyote.http11.Http11HeaderName.ACCEPT;
import static org.apache.coyote.http11.Http11HeaderName.CONTENT_LENGTH;
import static org.apache.coyote.http11.Http11HeaderName.COOKIE;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.http.HttpHeaders;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Http11RequestHeader {

    private static final String HEADER_DELIMITER = ": ";
    private static final String HEADER_VALUE_DELIMITER = ",";
    private static final int HEADER_KEY_INDEX = 0;
    private static final int HEADER_VALUE_INDEX = 1;

    private final HttpHeaders httpHeaders;

    private Http11RequestHeader(HttpHeaders httpHeaders) {
        this.httpHeaders = httpHeaders;
    }

    public static Http11RequestHeaderBuilder builder() {
        return new Http11RequestHeaderBuilder();
    }

    public static Http11RequestHeader from(BufferedReader bufferedReader) throws IOException {
        HttpHeaders httpHeaders = HttpHeaders.of(extractRequestHeader(bufferedReader), (s1, s2) -> true);
        return new Http11RequestHeader(httpHeaders);
    }

    private static Map<String, List<String>> extractRequestHeader(BufferedReader bufferedReader) {
        return bufferedReader.lines()
                .takeWhile(line -> !line.isBlank())
                .map(line -> line.split(HEADER_DELIMITER))
                .collect(Collectors.toMap(parts -> parts[HEADER_KEY_INDEX], Http11RequestHeader::extractHeaderValues));
    }

    private static List<String> extractHeaderValues(String[] parts) {
        if (parts.length < 2) {
            return List.of();
        }

        return Arrays.stream(parts[HEADER_VALUE_INDEX].split(HEADER_VALUE_DELIMITER))
                .map(String::trim)
                .toList();
    }

    public List<String> getAcceptType() {
        return httpHeaders.allValues(ACCEPT.getName());
    }

    public int getContentLength() {
        String contentLength = httpHeaders.firstValue(CONTENT_LENGTH.getName()).orElse("0");
        return Integer.parseInt(contentLength);
    }

    public String getCookie() {
        return httpHeaders.firstValue(COOKIE.getName()).orElseGet(() -> "");
    }

    public static class Http11RequestHeaderBuilder {
        private final Map<String, List<String>> headers;

        public Http11RequestHeaderBuilder() {
            this.headers = new java.util.HashMap<>();
        }

        public Http11RequestHeaderBuilder addHeader(String key, List<String> values) {
            headers.put(key, values);
            return this;
        }

        public Http11RequestHeader build() {
            HttpHeaders httpHeaders = HttpHeaders.of(headers, (s1, s2) -> true);
            return new Http11RequestHeader(httpHeaders);
        }
    }

    @Override
    public String toString() {
        return "Http11RequestHeader{" +
                "httpHeaders=" + httpHeaders +
                '}';
    }
}
