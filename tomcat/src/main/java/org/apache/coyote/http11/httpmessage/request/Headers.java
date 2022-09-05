package org.apache.coyote.http11.httpmessage.request;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class Headers {

    private final Map<String, String> headers;

    private Headers(Map<String, String> headers) {
        this.headers = headers;
    }

    public static Headers of(List<String> headerLines) {
        Map<String, String> headers = new LinkedHashMap<>();

        for (String header : headerLines) {
            String[] keyValue = header.split(": ");
            headers.put(keyValue[0], keyValue[1]);
        }

        return new Headers(headers);
    }


    public Map<String, String> getHeaders() {
        return Collections.unmodifiableMap(headers);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Headers that = (Headers) o;
        return Objects.equals(headers, that.headers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(headers);
    }

    @Override
    public String toString() {
        return headers.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .collect(Collectors.joining("\r\n"));
    }

    public String getHeader(String key) {
        return headers.get(key);
    }
}
