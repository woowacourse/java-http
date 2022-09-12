package org.apache.coyote.http;

import static java.lang.String.format;

import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.coyote.status.HttpCookie;
import support.StringUtils;

public class HttpResponse {

    private String responseFirstLine;
    private Map<String, String> headers = new LinkedHashMap<>();
    private String body;

    public HttpResponse() {
    }

    public HttpResponse addStatus(HttpStatus status) {
        responseFirstLine = HttpMessage.HTTP_1_1_STATUS.apply(status);
        return this;
    }

    public HttpResponse addCookie(final HttpCookie cookie) {
        final String value = HttpMessage.SET_COOKIE.apply(cookie);
        headers.put(HttpMessage.SET_COOKIE.type(), "JSESSIONID="+value);
        return this;
    }

    public HttpResponse add(HttpMessage header, Object value) {
        headers.put(header.type(), header.apply(value.toString()));
        return this;
    }

    public HttpResponse add(HttpMessage header, String value) {
        headers.put(header.type(), header.apply(value));
        return this;
    }

    public HttpResponse body(String body) {
        add(HttpMessage.CONTENT_LENGTH, body.getBytes().length);
        this.body = body;
        return this;
    }

    // "/index.html"
    public HttpResponse sendRedirect(String uri) {
        addStatus(HttpStatus.FOUND);
        headers.put(HttpMessage.LOCATION.type(), uri);
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
