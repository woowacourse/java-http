package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.coyote.http11.HttpCookie;

public class HttpRequestHeader {

    private static final int NAME_INDEX = 0;
    private static final int VALUE_INDEX = 1;
    private static final String FORM_DATA_CONTENT_TYPE = "application/x-www-form-urlencoded";

    private final Map<String, Object> headers;

    private HttpRequestHeader(final Map<String, Object> headers) {
        this.headers = headers;
    }

    public static HttpRequestHeader from(final List<String> rawHeader) {
        final Map<String, Object> parsedHeaders = new HashMap<>();
        for (final String header : rawHeader) {
            parseRawHeader(parsedHeaders, header);
        }

        return new HttpRequestHeader(parsedHeaders);
    }

    private static void parseRawHeader(final Map<String, Object> parsedHeaders, final String header) {
        final String[] parsedHeader = header.split(": ");
        final String headerName = parsedHeader[NAME_INDEX];
        final String headerValue = parsedHeader[VALUE_INDEX].trim();

        if (headerName.equals("Cookie")) {
            parsedHeaders.put(headerName, HttpCookie.from(headerValue));
            return;
        }
        parsedHeaders.put(headerName, headerValue);
    }

    public boolean isFormDataType() {
        return headers.containsKey("Content-Type") && headers.get("Content-Type").equals(FORM_DATA_CONTENT_TYPE);
    }

    public boolean contains(final String headerName) {
        return headers.containsKey(headerName);
    }

    public String getHeader(final String headerName) {
        return (String) headers.get(headerName);
    }

    public HttpCookie getCookies() {
        if (headers.containsKey("Cookie")) {
            return (HttpCookie) headers.get("Cookie");
        }
        return HttpCookie.createEmptyCookie();
    }
}
