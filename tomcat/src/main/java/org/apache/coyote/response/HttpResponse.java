package org.apache.coyote.response;

import org.apache.coyote.common.ContentType;
import org.apache.coyote.common.HttpCookie;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.apache.coyote.common.Headers.*;

public class HttpResponse {

    private static final String DEFAULT_VERSION = "HTTP/1.1";

    private final StatusLine statusLine;
    private final HttpResponseHeader headers;

    private String body;

    private HttpResponse(StatusLine statusLine, HttpResponseHeader headers, String body) {
        this.statusLine = statusLine;
        this.headers = headers;
        this.body = body;
    }

    public static HttpResponse createDefaultResponse() {
        HttpStatus httpStatus = HttpStatus.OK;
        Map<String, String> headers = new LinkedHashMap<>();
        String body = "";

        return new HttpResponse(new StatusLine(DEFAULT_VERSION, httpStatus), new HttpResponseHeader(headers), body);
    }

    public String getResponse() {
        return statusLine.getStatusLine() + CRLF + headers.getResponseHeader() + CRLF + body;
    }

    public void setContentType(ContentType contentType) {
        this.headers.add(CONTENT_TYPE, contentType.getType() + CHARSET_UTF8);
    }

    public void setBody(String body) {
        addHeader(CONTENT_LENGTH, String.valueOf(body.length()));
        this.body = body;
    }

    public void setStatus(HttpStatus status) {
        this.statusLine.setStatus(status);
    }

    public void addHeader(String key, String value) {
        this.headers.add(key, value);
    }

    public void setCookie(HttpCookie cookie) {
        this.headers.add(SET_COOKIE, cookie.convertToHeader());
    }
}
