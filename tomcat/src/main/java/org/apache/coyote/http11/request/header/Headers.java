package org.apache.coyote.http11.request.header;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;

public class Headers {

    private final Map<HeaderKey, String> entries;

    public Headers(final Map<HeaderKey, String> entries) {
        this.entries = entries;
    }

    public static Headers of(final List<String> headerLines) {
        final Map<HeaderKey, String> entries = headerLines.stream()
                .filter(HeaderKey::existsInLine)
                .collect(toMap(HeaderKey::from, HeaderKey::removeKeyFromLine));

        return new Headers(entries);
    }

    public void addHeader(final HeaderKey key, final String value) {
        entries.put(key, value);
    }

    public List<String> toList() {
        return entries.entrySet().stream()
                .map(this::toHeader)
                .collect(Collectors.toList());
    }

    private String toHeader(final Map.Entry<HeaderKey, String> entry) {
        final HeaderKey key = entry.getKey();
        final String value = entry.getValue();

        return key.toHeader(value);
    }

    public Cookies getCookies() {
        final String cookieValue = entries.get(HeaderKey.COOKIE);
        return Cookies.from(cookieValue);
    }
}
