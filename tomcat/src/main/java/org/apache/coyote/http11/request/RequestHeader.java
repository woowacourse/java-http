package org.apache.coyote.http11.request;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toMap;
import static org.apache.coyote.http11.common.Constants.CRLF;
import static org.apache.coyote.http11.common.Constants.EMPTY;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.common.HttpCookie;

public class RequestHeader {

    private static final String DELIMITER = ": ";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;
    private static final String COOKIE_HEADER = "Cookie";

    private final Map<String, String> items = new HashMap<>();

    private RequestHeader(final Map<String, String> items) {
        this.items.putAll(items);
    }

    public static RequestHeader from(final String headers) {
        return Arrays.stream(headers.split(CRLF))
                .map(header -> header.split(DELIMITER))
                .collect(collectingAndThen(
                        toMap(header -> header[KEY_INDEX].strip(), header -> header[VALUE_INDEX].strip()),
                        RequestHeader::new
                ));
    }

    public HttpCookie parseCookie() {
        final String cookie = items.getOrDefault(COOKIE_HEADER, EMPTY);
        return HttpCookie.from(cookie);
    }

    public String get(final String key) {
        return items.get(key);
    }

    public Map<String, String> getItems() {
        return items;
    }
}
