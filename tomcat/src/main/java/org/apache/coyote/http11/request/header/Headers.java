package org.apache.coyote.http11.request.header;

import org.apache.coyote.http11.cookie.Cookie;
import org.apache.coyote.http11.response.header.Header;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.apache.coyote.http11.common.Constant.HEADER_SEPARATOR;

public class Headers {

    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> headers;

    private Headers(final Map<String, String> headers) {
        this.headers = headers;
    }

    public static Headers from(final List<String> request) {
        Map<String, String> headers = request.stream()
                .map(header -> header.split(HEADER_SEPARATOR))
                .collect(Collectors.toMap(
                        key -> key[KEY_INDEX],
                        value -> value[VALUE_INDEX]
                ));

        return new Headers(headers);
    }

    public Optional<String> getHeader(final Header header) {
        final String key = header.getName();

        if (headers.containsKey(key)) {
            return Optional.of(headers.get(key));
        }

        return Optional.empty();
    }

    public Cookie getCookie() {
        Optional<String> cookie = getHeaderValue(Header.COOKIE);

        return cookie.map(Cookie::from)
                .orElseGet(Cookie::createDefault);

    }

    public Optional<String> getHeaderValue(final Header header) {
        String key = header.getName();

        if (headers.containsKey(key)) {
            return Optional.of(headers.get(key));
        }

        return Optional.empty();
    }
}
