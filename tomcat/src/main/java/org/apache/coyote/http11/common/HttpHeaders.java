package org.apache.coyote.http11.common;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HttpHeaders {

    private static final String KEY_VALUE_DELIMITER = ": ";
    private static final int KEY = 0;
    private static final int VALUE = 1;

    private final Map<HeaderKeys, String> headers;
    private final HttpCookie cookie;

    private HttpHeaders(final Map<HeaderKeys, String> headers, final HttpCookie cookie) {
        this.headers = headers;
        this.cookie = cookie;
    }

    public static HttpHeaders init() {
        return new HttpHeaders(new LinkedHashMap<>(), HttpCookie.init());
    }

    public static HttpHeaders from(final List<String> messages) {
        final Map<HeaderKeys, String> headers = new LinkedHashMap<>();
        HttpCookie cookie = HttpCookie.init();
        for (final String message : messages) {
            final String[] headerElement = message.split(KEY_VALUE_DELIMITER);
            if (HeaderKeys.isCookie(headerElement[KEY])) {
                cookie = HttpCookie.init(headerElement[VALUE]);
                continue;
            }
            headers.put(HeaderKeys.from(headerElement[KEY]), headerElement[VALUE]);
        }
        return new HttpHeaders(headers, cookie);
    }

    public HttpHeaders add(final HeaderKeys key, final String value) {
        this.headers.put(key, value);
        return this;
    }

    public String toMessage() {
        final StringBuilder message = new StringBuilder();
        for (Map.Entry<HeaderKeys, String> entry : headers.entrySet()) {
            message.append(entry.getKey().getName())
                .append(KEY_VALUE_DELIMITER)
                .append(entry.getValue())
                .append(HttpMessageDelimiter.WORD.getValue())
                .append(HttpMessageDelimiter.LINE.getValue());
        }
        excludeLastEmpty(message);
        return new String(message);
    }

    private void excludeLastEmpty(final StringBuilder message) {
        message.deleteCharAt(message.length() - 1);
        message.deleteCharAt(message.length() - 1);
    }

    public int getContentLength() {
        final String contentLength = headers.get(HeaderKeys.CONTENT_LENGTH);
        if (contentLength == null) {
            return 0;
        }
        return Integer.parseInt(contentLength);
    }
}
