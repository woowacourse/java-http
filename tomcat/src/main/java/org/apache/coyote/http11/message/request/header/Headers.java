package org.apache.coyote.http11.message.request.header;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.coyote.http11.message.Regex;
import org.apache.coyote.http11.message.header.Header;

public class Headers {

    private static final int INDEX_NAME = 0;
    private static final int INDEX_VALUE = 1;

    private final Map<String, String> values;

    private Headers(final Map<String, String> values) {
        this.values = values;
    }

    public static Headers from(final List<String> headerLines) {
        final Map<String, String> values = headerLines.stream()
                .map(line -> line.split(Regex.HEADER_VALUE.getValue(), 2))
                .collect(Collectors.toMap(
                        splitLine -> splitLine[INDEX_NAME],
                        splitLine -> splitLine[INDEX_VALUE]
                ));
        return new Headers(values);
    }

    public Cookie getCookie() {
        final Optional<String> rawCookie = get(Header.COOKIE);
        if (rawCookie.isEmpty()) {
            return Cookie.ofEmpty();
        }
        return Cookie.from(rawCookie.get());
    }

    public Optional<String> get(final Header header) {
        final String key = header.getName();
        if (values.containsKey(key)) {
            return Optional.of(values.get(key));
        }
        return Optional.empty();
    }
}
