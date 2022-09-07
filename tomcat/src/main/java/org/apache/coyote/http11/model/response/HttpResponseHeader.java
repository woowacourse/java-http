package org.apache.coyote.http11.model.response;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.coyote.http11.model.HttpHeaderType;

public class HttpResponseHeader {

    private static final String JSESSIONID_HEADER_PREFIX = "JSESSIONID=";
    private static final String LINE_BREAK = " \r\n";
    private static final String KEY_VALUE_SEPARATOR = ": ";

    private final Map<String, String> values;

    public HttpResponseHeader() {
        this.values = new LinkedHashMap<>();
    }

    public void addValue(String headerName, String headerValue) {
        values.put(headerName, headerValue);
    }

    public void addCookie(String cookie) {
        values.put(HttpHeaderType.SET_COOKIE, JSESSIONID_HEADER_PREFIX + cookie);
    }

    public String toResponse() {
        StringBuilder headerResponse = new StringBuilder();
        for (String headerName : values.keySet()) {
            headerResponse.append(headerName)
                    .append(KEY_VALUE_SEPARATOR)
                    .append(values.get(headerName))
                    .append(LINE_BREAK);
        }
        return headerResponse.toString();
    }

    public String getHeader(String headerName) {
        return values.get(headerName);
    }
}
