package org.apache.coyote.http11.request;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toUnmodifiableMap;
import static org.apache.coyote.http11.common.Constants.CRLF;

import java.util.Arrays;
import java.util.Map;

public class HttpRequestHeader {

    private static final String DELIMITER = ": ";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> headers;

    private HttpRequestHeader(final Map<String, String> headers) {
        this.headers = headers;
    }

    public static HttpRequestHeader from(final String requestHeader) {
        return Arrays.stream(requestHeader.split(CRLF))
                .map(header -> header.split(DELIMITER))
                .collect(collectingAndThen(
                        toUnmodifiableMap(header -> header[KEY_INDEX], header -> header[VALUE_INDEX]),
                        HttpRequestHeader::new
                ));
    }

    public String get(final String key) {
        return headers.getOrDefault(key, "none");
    }

}
