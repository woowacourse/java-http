package org.apache.coyote.http11.request;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.coyote.http11.HttpHeaderKey;

public class RequestHeaders {

    private static final String HEADER_DELIMITER = ": ";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<HttpHeaderKey, String> headers;

    public RequestHeaders(List<String> headers) {
        this.headers = headers.stream()
                .map(header -> header.split(HEADER_DELIMITER))
                .map(parts -> createHeaderEntry(parts[KEY_INDEX], parts[VALUE_INDEX]))
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private Map.Entry<HttpHeaderKey, String> createHeaderEntry(String key, String value) {
        return HttpHeaderKey.findByName(key)
                .map(headerKey -> Map.entry(headerKey, value))
                .orElse(null);
    }

    public String getCookieString() {
        return headers.getOrDefault(HttpHeaderKey.SET_COOKIE, null);
    }

    public int getContentLength() {
        return Integer.parseInt(headers.get(HttpHeaderKey.CONTENT_LENGTH));
    }
}
