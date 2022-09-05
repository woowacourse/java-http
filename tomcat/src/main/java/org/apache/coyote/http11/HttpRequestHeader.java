package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;

public class HttpRequestHeader {

    private static final String COOKIES_BOUNDARY = "; ";
    private static final String COOKIE_KEY_VALUE_BOUNDARY = "=";
    private static final String HEADER_KEY_VALUE_BOUNDARY = ": ";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String JSESSIONID = "JSESSIONID";
    private static final String COOKIE = "Cookie";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> values;

    public HttpRequestHeader(final List<String> requestHeaders) {
        this.values = initValues(requestHeaders);
    }

    private Map<String, String> initValues(final List<String> requestHeaders) {
        if (requestHeaders.isEmpty()) {
            return new HashMap<>();
        }

        final Map<String, String> result = new HashMap<>();

        for (final String requestHeader : requestHeaders) {
            final String[] keyValuePair = requestHeader.split(HEADER_KEY_VALUE_BOUNDARY);
            result.put(keyValuePair[KEY_INDEX], keyValuePair[VALUE_INDEX]);
        }

        return result;
    }

    public int findContentLength() {
        final String contentLength = values.get(CONTENT_LENGTH);
        if (StringUtils.isBlank(contentLength)) {
            return 0;
        }
        return Integer.parseInt(contentLength);
    }

    public Optional<String> findJSessionId() {
        final String cookie = values.get(COOKIE);

        if (StringUtils.isBlank(cookie)) {
            return Optional.empty();
        }

        final String[] cookieInfos = cookie.split(COOKIES_BOUNDARY);
        for (final String cookieInfo : cookieInfos) {
            final String[] keyValuePair = cookieInfo.split(COOKIE_KEY_VALUE_BOUNDARY);
            if (JSESSIONID.equals(keyValuePair[KEY_INDEX])) {
                return Optional.of(keyValuePair[VALUE_INDEX]);
            }
        }

        return Optional.empty();
    }
}
