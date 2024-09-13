package org.apache.coyote.http11.message.response;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.catalina.session.Session;
import org.apache.coyote.http11.message.HttpCookie;
import org.apache.coyote.http11.message.HttpHeaderName;
import org.apache.coyote.http11.message.HttpHeaders;
import org.apache.coyote.http11.message.file.StaticResourceConvertor;

public class HttpResponse {

    private static final byte[] EMPTY_BODY = new byte[0];
    private static final String TEXT_PLAIN_CHARSET_UTF_8 = "text/plain;charset=utf-8";

    private HttpStatus status;
    private final HttpHeaders headers;
    private byte[] body;

    public HttpResponse(HttpStatus status, HttpHeaders headers, byte[] body) {
        this.status = status;
        this.headers = headers;
        this.body = body;
    }

    public HttpResponse() {
        this(HttpStatus.OK, new HttpHeaders(), EMPTY_BODY);
    }

    public static HttpResponse of(HttpStatus status, byte[] body) {
        HttpHeaders headers = new HttpHeaders(Map.of(
                HttpHeaderName.CONTENT_LENGTH.getName(), List.of(java.lang.String.valueOf(body.length))
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

    public void setFound(String location) {
        setStatus(HttpStatus.FOUND);
        setHeader(HttpHeaderName.LOCATION, location);
    }

    public void setBodyFromStaticResource(String resourceName) {
        setStatus(HttpStatus.OK);
        try {
            Path resourcePath = StaticResourceConvertor.convertToPath(resourceName);
            setBody(Files.readAllBytes(resourcePath));
            setHeader(HttpHeaderName.CONTENT_TYPE, StaticResourceConvertor.probeContentType(resourcePath));
        } catch (IOException e) {
            throw new IllegalArgumentException(resourceName + " 파일을 읽는데 문제가 발생했습니다.");
        }
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

    public void setBody(byte[] body) {
        this.body = body;
        setHeader(HttpHeaderName.CONTENT_LENGTH, String.valueOf(body.length));
    }

    public void setBody(String rawBody) {
        this.body = rawBody.getBytes();
        setHeader(HttpHeaderName.CONTENT_LENGTH, String.valueOf(body.length));
        setHeader(HttpHeaderName.CONTENT_TYPE, TEXT_PLAIN_CHARSET_UTF_8);
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
}
