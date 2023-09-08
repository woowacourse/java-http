package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpRequestHeaders {

    public static final String DELIMITER = ": ";
    public static final int KEY_INDEX = 0;
    public static final int VALUE_INDEX = 1;
    public static final String COOKIE_HEADER = "Cookie";
    private final Map<String, String> headers;

    private HttpRequestHeaders(final Map<String, String> headers) {
        this.headers = headers;
    }

    public static HttpRequestHeaders from(final List<String> headers) {
        final HashMap<String, String> httpHeaders = new HashMap<>();
        headers.forEach(
                header -> httpHeaders.put(header.split(DELIMITER)[KEY_INDEX], header.split(DELIMITER)[VALUE_INDEX]));

        return new HttpRequestHeaders(httpHeaders);
    }

    public String getValue(final String header) {
        return headers.get(header);
    }

    public boolean contains(final String header) {
        return headers.containsKey(header);
    }

    public HttpCookie getCookie() {
        if (headers.containsKey(COOKIE_HEADER)) {
            return HttpCookie.from(headers.get(COOKIE_HEADER));
        }
        return null;
    }
}
