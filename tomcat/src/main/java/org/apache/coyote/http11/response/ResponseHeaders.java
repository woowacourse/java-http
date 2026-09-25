package org.apache.coyote.http11.response;

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

    public void setContentType(final String contentType) {
        values.put(CONTENT_TYPE, contentType + CHARSET);
    }

    public void setContentLength(final int contentLength) {
        values.put(CONTENT_LENGTH, String.valueOf(contentLength));
    }

    public void setLocation(final String location) {
        values.put(LOCATION, location);
    }

    public void setCookie(final String cookie) {
        values.put(SET_COOKIE, cookie);
    }

    public List<String> toLines() {
        final List<String> lines = new ArrayList<>();
        for (final Map.Entry<String, String> value : values.entrySet()) {
            lines.add(value.getKey() + KEY_VALUE_SEPARATOR + value.getValue() + LINE_SUFFIX);
        }
        return lines;
    }
}
