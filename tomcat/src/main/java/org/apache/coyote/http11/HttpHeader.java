package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class HttpHeader {

    private static final String DELIMITER = ":";
    private static final String CRLF = "\r\n";

    private final Map<String, String> headers;

    public HttpHeader() {
        this.headers = new HashMap<>();
    }

    public HttpHeader(Map<String, String> headers) {
        this.headers = headers;
    }

    public HttpHeader(List<String> rawHeaders) {
        if (rawHeaders == null) {
            throw new UncheckedServletException("헤더는 null일 수 없습니다.");
        }

        this.headers = rawHeaders.stream()
                .collect(Collectors.toMap(this::parseFieldName, this::parseFieldValue));
    }

    private String parseFieldName(String rawHeader) {
        validateHeaderFormat(rawHeader);
        return rawHeader.substring(0, rawHeader.indexOf(DELIMITER));
    }

    private String parseFieldValue(String rawHeader) {
        validateHeaderFormat(rawHeader);
        return rawHeader.substring(rawHeader.indexOf(DELIMITER) + DELIMITER.length())
                .trim();
    }

    private void validateHeaderFormat(String rawHeader) {
        if (rawHeader == null || rawHeader.isBlank()) {
            throw new UncheckedServletException("형식이 올바르지 않은 헤더가 포함되어 있습니다.");
        }
        if (!rawHeader.contains(DELIMITER)) {
            throw new UncheckedServletException("형식이 올바르지 않은 헤더가 포함되어 있습니다.");
        }
        if (rawHeader.startsWith(DELIMITER)) {
            throw new UncheckedServletException("형식이 올바르지 않은 헤더가 포함되어 있습니다.");
        }
    }

    public Optional<String> find(String name) {
        return Optional.ofNullable(headers.get(name));
    }

    public String get(String name) {
        return find(name).orElseThrow(() -> new UncheckedServletException(name + " 헤더가 존재하지 않습니다."));
    }

    public boolean contains(String name) {
        return headers.containsKey(name);
    }

    public void add(String name, String value) {
        headers.put(name, value);
    }

    public String buildMessage() {
        return headers.keySet().stream()
                .map(key -> key + DELIMITER + " " + headers.get(key))
                .collect(Collectors.joining(CRLF));
    }
}
