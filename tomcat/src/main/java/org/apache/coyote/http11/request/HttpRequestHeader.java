package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.coyote.http11.HttpCookie;

public class HttpRequestHeader {

    private static final int NAME_INDEX = 0;
    private static final int VALUE_INDEX = 1;
    private static final String FORM_DATA_CONTENT_TYPE = "application/x-www-form-urlencoded";

    private final Map<String, String> headers;
    private final HttpCookie cookies;

    private HttpRequestHeader(final Map<String, String> headers, final HttpCookie cookies) {
        this.headers = headers;
        this.cookies = cookies;
    }

    public static HttpRequestHeader of(final List<String> rawHeader) {
        final HttpCookie httpCookie = getCookies(rawHeader);

        final Map<String, String> parsedHeaders = new HashMap<>();
        for (final String header : rawHeader) {
            parseRawHeader(parsedHeaders, header);
        }

        return new HttpRequestHeader(parsedHeaders, httpCookie);
    }

    private static HttpCookie getCookies(final List<String> rawHeader) {
        return HttpCookie.of(rawHeader.stream()
                .filter(it -> it.contains("Cookie: "))
                .findFirst()
                .orElseGet(String::new));
    }

    private static void parseRawHeader(final Map<String, String> parsedHeaders, final String header) {
        final String[] parsedHeader = header.split(": ");
        final String headerName = parsedHeader[NAME_INDEX];
        final String headerValue = parsedHeader[VALUE_INDEX].trim();

        if (!headerName.equals("Cookie")) {
            parsedHeaders.put(headerName, headerValue);
        }
    }

    public boolean isFormDataType() {
        return headers.containsKey("Content-Type") && headers.get("Content-Type").equals(FORM_DATA_CONTENT_TYPE);
    }

    public boolean contains(final String headerName) {
        return headers.containsKey(headerName);
    }

    public String getHeader(final String headerName) {
        return headers.get(headerName);
    }

    public HttpCookie getCookies() {
        return cookies;
    }
}
