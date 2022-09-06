package nextstep.jwp.http;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class HttpHeaders {

    private static final String HEADER_DELIMITER_REGEX = ": ?";
    private static final int HEADER_SPLIT_LIMIT = 2;
    private static final int HEADER_TYPE_INDEX = 0;
    private static final int HEADER_VALUE_INDEX = 1;
    private static final int ZERO_CONTENT_LENGTH = 0;
    private static final String CONTENT_TYPE_HEADER = "Content-Type";
    private static final String CONTENT_LENGTH_HEADER = "Content-Length";
    private static final String COOKIE_HEADER = "COOKIE";

    private final Map<String, String> headers;

    public HttpHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public static HttpHeaders parse(List<String> lines) {
        Map<String, String> headers = new LinkedHashMap<>();
        for (String line : lines) {
            List<String> parsingLine = List.of(
                line.split(HEADER_DELIMITER_REGEX, HEADER_SPLIT_LIMIT));
            String type = parsingLine.get(HEADER_TYPE_INDEX);
            String value = parsingLine.get(HEADER_VALUE_INDEX);
            headers.put(type, value);
        }
        return new HttpHeaders(headers);
    }

    public Optional<String> get(String key) {
        String value = headers.get(key);
        return Optional.ofNullable(value);
    }

    public boolean matchesContentType(String contentType) {
        return get(CONTENT_TYPE_HEADER).stream()
            .anyMatch(it -> it.equals(contentType));
    }

    public int getContentLength() {
        return get(CONTENT_LENGTH_HEADER)
            .map(Integer::parseInt)
            .orElse(ZERO_CONTENT_LENGTH);
    }

    public Cookie parseCookie() {
        return get(COOKIE_HEADER)
            .map(Cookie::parse)
            .orElseGet(Cookie::empty);
    }
}
