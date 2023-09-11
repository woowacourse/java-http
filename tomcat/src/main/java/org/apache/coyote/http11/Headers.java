package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.apache.coyote.ResponseHeader.CONTENT_LENGTH;
import static org.apache.coyote.ResponseHeader.CONTENT_TYPE;
import static org.apache.coyote.http11.httpresponse.HttpResponse.BLANK;
import static org.apache.coyote.http11.httpresponse.HttpResponse.CRLF;
import static org.apache.coyote.http11.httpresponse.HttpResponse.EMPTY;

public class Headers {

    private final Map<String, String> keyValues;

    public Headers() {
        this(new HashMap<>());
    }

    public Headers(final Map<String, String> keyValues) {
        this.keyValues = keyValues;
    }

    public String join() {
        if (containsContentHeader()) {
            String contentLength = keyValues.remove(CONTENT_LENGTH.getName());
            String contentType = keyValues.remove(CONTENT_TYPE.getName());

            return headerWithCrlf() + toString(CONTENT_LENGTH.getName(), contentLength)
                    + CRLF + toString(CONTENT_TYPE.getName(), contentType);
        }
        return joinHeaders();
    }

    private String headerWithCrlf() {
        String headers = joinHeaders();
        if (headers.isEmpty()) {
            return EMPTY;
        }
        return headers + CRLF;
    }

    private String joinHeaders() {
        return keyValues.entrySet().stream()
                .map(entry -> toString(entry.getKey(), entry.getValue()))
                .collect(Collectors.joining(CRLF));
    }

    private boolean containsContentHeader() {
        return keyValues.containsKey(CONTENT_LENGTH.getName())
                && keyValues.containsKey(CONTENT_TYPE.getName());
    }

    private String toString(String key, String value) {
        return key + ": " + value + BLANK;
    }

    public void put(String key, String value) {
        keyValues.put(key, value);
    }

    public String get(String key) {
        return keyValues.getOrDefault(key, null);
    }

    public void remove(String key) {
        keyValues.remove(key);
    }

}
