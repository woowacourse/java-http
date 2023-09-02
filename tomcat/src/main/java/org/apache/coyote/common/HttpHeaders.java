package org.apache.coyote.common;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HttpHeaders {

    private final Map<String, Header> headers;

    public HttpHeaders() {
        this.headers = new LinkedHashMap<>();
    }

    public void addHeader(String key, String value) {
        Header header = headers.computeIfAbsent(key, ignore -> new CommaSeparateHeader());
        header.add(value);
    }

    public void addHeader(String key, List<String> values) {
        Header header = headers.computeIfAbsent(key, ignore -> new CommaSeparateHeader());
        header.addAll(values);
    }

    public void setHeader(String key, String value) {
        Header header = new CommaSeparateHeader();
        header.add(value);
        headers.put(key, header);
    }

    public void setContentType(HttpContentType contentType) {
        setHeader("Content-Type", contentType.getValue());
    }

    public void setHeader(String key, List<String> values) {
        Header header = new CommaSeparateHeader();
        header.addAll(values);
        headers.put(key, header);
    }

    public void setCookie(HttpCookie cookie) {
        Header header = new SemicolonSeparateHeader();
        header.add(cookie.getName() + "=" + cookie.getValue());
        if (cookie.isHttpOnly()) {
            header.add("HttpOnly");
        }
        if (cookie.isSecure()) {
            header.add("Secure");
        }
        if (!cookie.getMaxAge().isNegative()) {
            header.add("Max-Age=" + cookie.getMaxAge().getSeconds());
        }
        headers.put("Set-Cookie", header);
    }

    public String getHeader(String key) {
        Header header = headers.getOrDefault(key, CommaSeparateHeader.EMPTY);
        return header.getValues();
    }

    public Map<String, Header> getHeaders() {
        return Collections.unmodifiableMap(headers);
    }
}
