package org.apache.coyote.http11.response;

import org.apache.coyote.http11.Cookie;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ResponseHeaders {

    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String LOCATION = "Location";
    private static final String SET_COOKIE = "Set-Cookie";
    private static final String CHARSET = ";charset=utf-8";
    private static final String KEY_VALUE_SEPARATOR = ": ";
    private static final String LINE_SUFFIX = " ";

    private final Map<String, String> values = new LinkedHashMap<>();
    private final List<Cookie> cookies = new ArrayList<>();

    public void setHeader(final String name, final String value) {
        values.put(name, value);
    }

    public void setContentType(final String contentType) {
        setHeader(CONTENT_TYPE, contentType + CHARSET);
    }

    public void setContentLength(final int contentLength) {
        setHeader(CONTENT_LENGTH, String.valueOf(contentLength));
    }

    public void setLocation(final String location) {
        setHeader(LOCATION, location);
    }

    public void addCookie(final Cookie cookie) {
        cookies.add(cookie);
    }

    public List<String> toLines() {
        final List<String> lines = new ArrayList<>();
        for (final Map.Entry<String, String> value : values.entrySet()) {
            lines.add(value.getKey() + KEY_VALUE_SEPARATOR + value.getValue() + LINE_SUFFIX);
        }
        for (final Cookie cookie : cookies) {
            lines.add(SET_COOKIE + KEY_VALUE_SEPARATOR + cookie.toMessage() + LINE_SUFFIX);
        }
        return lines;
    }
}
