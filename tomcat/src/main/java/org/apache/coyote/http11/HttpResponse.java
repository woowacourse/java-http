package org.apache.coyote.http11;

import org.apache.coyote.http11.header.ContentType;
import org.apache.coyote.http11.header.Cookies;
import org.apache.coyote.http11.header.Headers;
import org.apache.coyote.http11.header.HttpHeader;

public class HttpResponse {

    public static final String HTTP_VERSION = "HTTP/1.1";
    private static final String EMPTY_BODY = "";
    private static final String CRLF = "\r\n";

    private HttpStatus status;
    private String body;
    private final Headers headers = new Headers();

    public HttpResponse() {
        this.status = HttpStatus.OK;
        setContentType(new ContentType("text/html"));
    }

    public void redirectTo(final String location) {
        setStatus(HttpStatus.FOUND);
        setBody(EMPTY_BODY);
        headers.add(new HttpHeader("Location", location));
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

    public void putHeader(HttpHeader header) {
        this.headers.add(header);
    }

    public HttpHeader getHeader(String name) {
        return headers.get(name);
    }

    public Headers getHeaders() {
        return headers;
    }

    public void setContentType(ContentType contentType) {
        putHeader(contentType);
    }

    public void setCookies(Cookies cookies) {
        putHeader(cookies.toHeader("Set-Cookie"));
    }

    public String toLine() {
        return String.join(CRLF,
                HttpResponse.HTTP_VERSION + " " + status.toLine(),
                headers.toLine(),
                "",
                body
        );
    }
}
