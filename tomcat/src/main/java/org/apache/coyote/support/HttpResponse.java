package org.apache.coyote.support;

import static java.lang.String.format;

import jakarta.servlet.http.Cookie;
import java.util.LinkedHashMap;
import java.util.Map;
import support.StringUtils;

public class HttpResponse {

    private String responseFirstLine;
    private Map<String, String> headers = new LinkedHashMap<>();
    private String body;

    public HttpResponse() {
    }

    public HttpResponse addStatus(HttpStatus status) {
        responseFirstLine = HttpHeader.HTTP_1_1_STATUS.apply(status);
        return this;
    }

    public HttpResponse addCooke(final HttpCookie cookie) {
        final String value = HttpHeader.SET_COOKIE.apply(cookie);
        headers.put(HttpHeader.SET_COOKIE.type(), value);
        return this;
    }

    public HttpResponse add(HttpHeader header, Object value) {
        headers.put(header.type(), header.apply(value.toString()));
        return this;
    }

    public HttpResponse add(HttpHeader header, String value) {
        headers.put(header.type(), header.apply(value));
        return this;
    }

    public HttpResponse body(String body) {
        add(HttpHeader.CONTENT_LENGTH, body.getBytes().length);
        this.body = body;
        return this;
    }

    // "/index.html"
    public HttpResponse sendRedirect(String uri) {
        addStatus(HttpStatus.FOUND);
        headers.put(HttpHeader.LOCATION.type(), uri);
        return this;
    }

    public String toStringData() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(format("%s \r\n", responseFirstLine));
        appendHeader(stringBuffer);
        appendBody(stringBuffer);
        return stringBuffer.toString();
    }

    private void appendHeader(final StringBuffer stringBuffer) {
        for (final String key : headers.keySet()) {
            final String value = headers.get(key);
            stringBuffer.append(format("%s: %s \r\n", key, value));
        }
    }

    private void appendBody(final StringBuffer stringBuffer) {
        if (!StringUtils.isEmpty(body)) {
            stringBuffer.append("\r\n");
            stringBuffer.append(body);
        }
    }
}
