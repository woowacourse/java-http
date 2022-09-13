package org.apache.coyote.http11.response;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.HttpCookie;

public class HttpResponseHeader {

    private final Map<String, String> headers;
    private final HttpCookie cookies;

    public HttpResponseHeader(final Map<String, String> headers, final HttpCookie cookies) {
        this.headers = headers;
        this.cookies = cookies;
    }

    public HttpResponseHeader(final Map<String, String> headers) {
        this(headers, new HttpCookie(new HashMap<>()));
    }

    public void addHeader(final String headerName, final String value) {
        headers.putIfAbsent(headerName, value);
    }

    public void addCookie(final String headerName, final String value) {
        cookies.addCookie(headerName, value);
    }

    public String getHeader(final String headerName) {
        return headers.get(headerName);
    }

    public HttpCookie getCookies() {
        return cookies;
    }

    @Override
    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(cookies.toString());

        for (final String name : headers.keySet()) {
            stringBuilder.append(name).append(": ").append(headers.get(name));
            appendCharsetToContentType(stringBuilder, name);
            stringBuilder.append(" \r\n");
        }

        return stringBuilder.toString();
    }

    private void appendCharsetToContentType(final StringBuilder stringBuilder, final String name) {
        if (name.equals("Content-Type")) {
            stringBuilder.append(";charset=utf-8");
        }
    }
}
