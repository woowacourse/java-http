package org.apache.coyote.http11.request;

import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.coyote.http11.web.Cookie;

public class HttpHeaders {

    private final Map<String, String> values;

    public HttpHeaders(final Map<String, String> values) {
        this.values = new LinkedHashMap<>(values);
    }

    public void setLocation(final String location) {
        values.put("Location", location);
    }

    public String toTextHeader() {
        StringBuilder sb = new StringBuilder();
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

    public Map<String, String> getValues() {
        return values;
    }

    public String getContentLength() {
        return values.get("Content-Length");
    }

    public void setContentType(final String contentType) {
        values.put("Content-Type", contentType + ";charset=utf-8");
    }

    public void setContentLength(final int contentLength) {
        values.put("Content-Length", String.valueOf(contentLength));
    }

    public void setCookie(final Cookie cookie) {
        values.put("Set-Cookie", cookie.toPair());
    }
}
