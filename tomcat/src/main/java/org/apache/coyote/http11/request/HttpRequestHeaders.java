package org.apache.coyote.http11.request;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpRequestHeaders {
    private static final String PAIR_DELIMITER = ": ";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> headers;

    private HttpRequestHeaders(final Map<String, String> headers) {
        this.headers = headers;
    }

    public static HttpRequestHeaders from(final List<String> httpRequestHeader) {
        return new HttpRequestHeaders(httpRequestHeader.stream()
                .map(data -> data.split(PAIR_DELIMITER))
                .collect(Collectors.toMap(
                        data -> data[KEY_INDEX],
                        data -> data[VALUE_INDEX])
                )
        );
    }

    public boolean hasCookie() {
        return headers.containsKey("Cookie");
    }

    public String get(final String key) {
        return headers.get(key);
    }
}
