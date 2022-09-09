package org.apache.coyote.http11.request;

import java.util.Map;
import utils.ParseUtils;

public class RequestHeaders {

    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String COOKIE = "Cookie";
    private static final String REGEX_1 = "\r\n";
    private static final String REGEX_2 = ": ";
    private static final String DEFAULT_LENGTH = "0";

    private final Map<String, String> headers;

    public RequestHeaders(final Map<String, String> headers) {
        this.headers = headers;
    }

    public static RequestHeaders from(final String headers) {
        return new RequestHeaders(ParseUtils.parse(headers, REGEX_1, REGEX_2));
    }

    public String getContentLength() {
        return headers.getOrDefault(CONTENT_LENGTH, DEFAULT_LENGTH);
    }

    public boolean hasCookies() {
        return headers.containsKey(COOKIE);
    }

    public String getCookies() {
        return headers.get(COOKIE);
    }
}
