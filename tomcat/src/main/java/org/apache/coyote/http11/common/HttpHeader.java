package org.apache.coyote.http11.common;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.StringJoiner;

public record HttpHeader(Map<String, String> headers) {

    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String HEADER_DELIMITER = ": ";
    private static final String MULTIPLE_HEADER_KEY_DELIMITER = ",";

    public static HttpHeader from(String rawHeaders) {
        HashMap<String, String> headers = Arrays.stream(rawHeaders.split(Constants.CRLF))
                .map(line -> line.split(HEADER_DELIMITER, 2))
                .filter(keyValue -> keyValue.length == 2)
                .collect(HashMap::new, (map, entry) -> map.put(entry[0], entry[1]), HashMap::putAll);
        return new HttpHeader(headers);
    }

    public Optional<String> get(String key) {
        return Optional.ofNullable(headers.get(key));
    }

    public OptionalInt getContentLength() {
        if (!headers.containsKey(CONTENT_LENGTH)) {
            return OptionalInt.empty();
        }
        return OptionalInt.of(Integer.parseInt(headers.get(CONTENT_LENGTH)));
    }

    public void add(String key, String value) {
        headers.computeIfPresent(key, (k, v) -> String.join(MULTIPLE_HEADER_KEY_DELIMITER, v, value));
        headers.putIfAbsent(key, value);
    }

    public String toString() {
        StringJoiner joiner = new StringJoiner(Constants.CRLF);
        headers.forEach((key, value) -> joiner.add(String.join("", key, HEADER_DELIMITER, value)));
        return joiner.toString();
    }
}
