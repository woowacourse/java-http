package org.apache.coyote.http11.response;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.Cookie;
import org.apache.coyote.http11.request.spec.HttpHeaders;
import org.apache.coyote.http11.response.spec.HttpStatus;

public class HttpResponse {

    private HttpStatus status;
    private HttpHeaders headers;
    private String body;

    public HttpResponse(HttpStatus status) {
        this.status = status;
        this.headers = initHeaders();
        this.body = "";
    }

    private HttpHeaders initHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Connection", "keep-alive");
        return new HttpHeaders(headers);
    }

    public void addHeader(String key, String value) {
        this.headers.add(key, value);
    }

    public String getHeader(String key) {
        return this.headers.get(key);
    }

    public byte[] getBytes() {
        return String.format("%s\r\n%s\r\n\r\n%s",
                        new StatusLine(status).toHttpResponse(),
                        this.headers.toHttpResponse(),
                        body)
                .getBytes(StandardCharsets.UTF_8);
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public HttpStatus getStatus() {
        return this.status;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public void addCookie(Cookie cookie) {
        headers.addCookie(cookie);
    }

    private static class StatusLine {
        private static final String DEFAULT_HTTP_PROTOCOL = "HTTP/1.1";

        private final HttpStatus httpStatus;

        public StatusLine(HttpStatus httpStatus) {
            this.httpStatus = httpStatus;
        }

        public String toHttpResponse() {
            return String.format("%s %d %s", DEFAULT_HTTP_PROTOCOL, httpStatus.getCode(), httpStatus.getReasonPhrase());
        }
    }
}
