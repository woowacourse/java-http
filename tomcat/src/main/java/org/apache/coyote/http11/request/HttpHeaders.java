package org.apache.coyote.http11.request;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.coyote.http11.web.Cookie;

public class HttpHeaders {

    private final Map<String, String> values;

    private HttpHeaders(final Map<String, String> values) {
        this.values = new LinkedHashMap<>(values);
    }

    public static HttpHeaders of(final List<String> headerLines) {
        final Map<String, String> headers = new LinkedHashMap<>();
        for (String value : headerLines) {
            final String[] header = value.split(": ");
            headers.put(header[0], header[1]);
        }
        return new HttpHeaders(headers);
    }

    public static HttpHeaders makeEmptyHeader() {
        return new HttpHeaders(Collections.emptyMap());
    }

    public String toTextHeader() {
        final StringBuilder sb = new StringBuilder();
        for (String s : values.keySet()) {
            sb.append(s).append(": ").append(values.get(s)).append(" \r\n");
        }
        return sb.toString();
    }

    public boolean hasContentLength() {
        return values.containsKey("Content-Length");
    }

    public String getLocation() {
        return values.get("Location");
    }

    public void setLocation(final String location) {
        values.put("Location", location);
    }

    public Map<String, String> getValues() {
        return values;
    }

    public String getContentLength() {
        return values.get("Content-Length");
    }

    public void setContentLength(final int contentLength) {
        values.put("Content-Length", String.valueOf(contentLength));
    }

    public void setContentType(final String contentType) {
        values.put("Content-Type", contentType + ";charset=utf-8");
    }

    public void setCookie(final Cookie cookie) {
        values.put("Set-Cookie", cookie.toPair());
    }

    public boolean hasCookie() {
        return values.containsKey("Cookie");
    }

    public String getCookieValue() {
        return values.get("Cookie");
    }
}
