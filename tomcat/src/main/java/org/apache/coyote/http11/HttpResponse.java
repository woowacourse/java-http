package org.apache.coyote.http11;

import static org.apache.coyote.http11.HttpHeaders.HTTP_LINE_SUFFIX;

import java.nio.charset.StandardCharsets;

public class HttpResponse {

    private HttpResponseStatusLine statusLine;
    private final HttpHeaders headers;
    private String body;

    public HttpResponse(HttpStatusCode statusCode, HttpHeaders headers, String body) {
        this.statusLine = new HttpResponseStatusLine(statusCode);
        this.headers = headers;
        this.body = body;
    }

    public static HttpResponse createBasicResponseFrom(HttpRequest request) {
        final var headers = HttpHeaders.createBasicResponseHeadersFrom(request);
        return new HttpResponse(HttpStatusCode.OK, headers, "");
    }

    public byte[] getBytes() {
        return toString().getBytes(StandardCharsets.UTF_8);
    }

    public void sendRedirect(String location) {
        setStatus(HttpStatusCode.FOUND);
        setHeader(HttpHeaders.LOCATION, location);
    }

    public void setStatus(HttpStatusCode statusCode) {
        this.statusLine = new HttpResponseStatusLine(statusCode);
    }

    public void setHeader(String key, String value) {
        headers.put(key, value);
    }

    public void setBody(String body) {
        headers.put(HttpHeaders.CONTENT_LENGTH, String.valueOf(body.getBytes(StandardCharsets.UTF_8).length));
        this.body = body;
    }

    @Override
    public String toString() {
        return String.join(HTTP_LINE_SUFFIX, statusLine.toString(), headers.toString(), body);
    }
}
