package org.apache.coyote.http11;

import org.apache.coyote.http11.header.ContentType;
import org.apache.coyote.http11.header.Cookies;
import org.apache.coyote.http11.header.HttpHeader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpResponse {

    public static final String HTTP_VERSION = "HTTP/1.1";
    private static final String EMPTY_BODY = "";

    private HttpStatus status;
    private String body;
    private final Map<String, HttpHeader> headers = new HashMap<>();

    public HttpResponse() {
        this.status = HttpStatus.OK;
        setContentType(new ContentType("text/html"));
    }

    public void redirectTo(final String location) {
        setStatus(HttpStatus.FOUND);
        setBody(EMPTY_BODY);
        putHeader(new HttpHeader("Location", location));
    }

    public void setStatus(final HttpStatus status) {
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setBody(String body) {
        this.body = body;
        if (!body.isEmpty()) {
            putHeader(new HttpHeader("Content-Length", String.valueOf(body.getBytes().length)));
        }
    }

    public String getBody() {
        return body;
    }

    public HttpHeader getHeader(String name) {
        return headers.get(name);
    }

    public void putHeader(HttpHeader header) {
        this.headers.put(header.getName(), header);
    }

    public void setContentType(ContentType contentType) {
        putHeader(contentType);
    }

    public List<HttpHeader> getHeaders() {
        return new ArrayList<>(headers.values());
    }

    public HttpResponse with(Cookies cookies) {
        putHeader(cookies.toHeader("Set-Cookie"));
        return this;
    }
}
