package org.apache.coyote.http11.message.response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.catalina.session.Session;
import org.apache.coyote.http11.message.HttpCookie;
import org.apache.coyote.http11.message.HttpHeaderName;
import org.apache.coyote.http11.message.HttpHeaders;

public class HttpResponse {

    private static final byte[] EMPTY_BODY = new byte[0];

    private HttpStatus status;
    private final HttpHeaders headers;
    private final byte[] body;

    public HttpResponse(HttpStatus status, HttpHeaders headers, byte[] body) {
        this.status = status;
        this.headers = headers;
        this.body = body;
    }

    public static HttpResponse of(HttpStatus status, byte[] body) {
        HttpHeaders headers = new HttpHeaders(Map.of(
                HttpHeaderName.CONTENT_LENGTH.getName(), List.of(String.valueOf(body.length))
        ));
        return new HttpResponse(status, headers, body);
    }

    public static HttpResponse from(HttpStatus status) {
        HttpHeaders headers = new HttpHeaders(new HashMap<>());
        return new HttpResponse(status, headers, EMPTY_BODY);
    }

    public static HttpResponse found(String location) {
        HttpResponse response = from(HttpStatus.FOUND);
        response.setHeader(HttpHeaderName.LOCATION, location);
        return response;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public byte[] getBody() {
        return body;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public Optional<String> getFieldByHeaderName(HttpHeaderName headerName) {
        return headers.getFieldByHeaderName(headerName);
    }

    public void setHeader(HttpHeaderName name, String field) {
        headers.setHeader(name, field);
    }

    public void setSessionCookie(Session session) {
        HttpCookie cookie = HttpCookie.from(session);
        headers.setHeader(HttpHeaderName.SET_COOKIE, cookie.stringify());

    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }
}
