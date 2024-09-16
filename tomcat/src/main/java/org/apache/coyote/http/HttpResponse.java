package org.apache.coyote.http;

import java.nio.charset.StandardCharsets;
import java.util.StringJoiner;

import org.apache.catalina.session.Session;

public class HttpResponse implements HttpComponent {

    private HttpStatusLine statusLine;
    private HttpHeaders headers;
    private HttpBody body;

    public HttpResponse() {
        statusLine = new HttpStatusLine(HttpStatusCode.OK);
        headers = new HttpHeaders();
        body = new HttpBody("");
    }

    public void sendRedirect(final String location) {
        setStatusCode(HttpStatusCode.FOUND);
        setLocation(location);
    }

    public HttpStatusLine getStatusLine() {
        return statusLine;
    }

    public void setStatusLine(final HttpStatusLine statusLine) {
        this.statusLine = statusLine;
    }

    public HttpStatusCode getStatusCode() {
        return statusLine.getStatusCode();
    }

    public void setStatusCode(final HttpStatusCode statusCode) {
        statusLine = new HttpStatusLine(statusCode);
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public void setHeaders(final HttpHeaders headers) {
        this.headers = headers;
    }

    public void setContentType(final String type) {
        headers.setContentType(type);
    }

    public Session getSession() {
        String sessionId = headers.getSessionId();
        return new Session(sessionId);
    }

    public void setCookie(final HttpCookie cookie) {
        headers.setCookie(cookie);
    }

    public void setLocation(final String location) {
        headers.setLocation(location);
    }

    public HttpBody getBody() {
        return body;
    }

    public void setBody(final HttpBody body) {
        this.body = body;
        headers.setContentLength(body.getLength());
    }

    public byte[] getBytes() {
        return asString().getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public String asString() {
        final var result = new StringJoiner(LINE_FEED);
        result.add(statusLine.asString())
                .add(headers.asString());
        if (body != null) {
            result.add(body.asString());
        }
        return result.toString();
    }
}
