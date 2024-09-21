package org.apache.coyote.http11.response;

import org.apache.coyote.http11.ResourceReader;
import org.apache.coyote.http11.headers.HeaderName;
import org.apache.coyote.http11.headers.HttpHeaders;

public class HttpResponse {

    private StatusLine statusLine;
    private final HttpHeaders headers;
    private String body;

    public HttpResponse(StatusLine statusLine, HttpHeaders headers, String body) {
        this.statusLine = statusLine;
        this.headers = headers;
        this.body = body;
    }

    public HttpResponse() {
        this.headers = new HttpHeaders();
    }

    public HttpResponse(StatusLine statusLine, HttpHeaders headers) {
        this.statusLine = statusLine;
        this.headers = headers;
    }

    public void sendRedirect(String location) {
        statusLine = StatusLine.createHttp11(HttpStatus.FOUND);
        body = ResourceReader.read(location);
        setLocation(location);
        setContentType(location);
        setContentLength(body);
    }

    public void setBody(String location) {
        body = ResourceReader.read(location);
        setContentType(location);
        setContentLength(body);
    }

    private void setLocation(String uri) {
        headers.add(HeaderName.LOCATION, "http://localhost:8080" + uri);
    }

    private void setContentType(String resourcePath) {
        String value = ResourceReader.probeContentType(resourcePath) + ";charset=utf-8";
        headers.add(HeaderName.CONTENT_TYPE, value);
    }

    private void setContentLength(String body) {
        String value = String.valueOf(body.getBytes().length);
        headers.add(HeaderName.CONTENT_LENGTH, value);
    }

    public void setStatus(HttpStatus status) {
        statusLine = StatusLine.createHttp11(status);
    }

    public void setSetCookie(String value) {
        headers.add(HeaderName.SET_COOKIE, value);
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public String render() {
        return statusLine + "\r\n" + headers + "\r\n\r\n" + body;
    }

    @Override
    public String toString() {
        return render();
    }

    public String getBody() {
        return body;
    }
}
