package org.apache.coyote.http11;

import static org.apache.coyote.http11.HttpHeaders.HTTP_LINE_SUFFIX;

import java.nio.charset.StandardCharsets;

public class HttpResponse {

    private final HttpResponseStatusLine statusLine;
    private final HttpHeaders headers;
    private final String body;

    public HttpResponse(HttpStatusCode statusCode, HttpHeaders headers, String body) {
        this.statusLine = new HttpResponseStatusLine(statusCode);
        this.headers = headers;
        this.body = body;
    }

    public void setHeaders(String key, String value) {
        headers.put(key, value);
    }

    public byte[] getBytes() {
        return toString().getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public String toString() {
        return String.join(HTTP_LINE_SUFFIX, statusLine.toString(), headers.toString(), body);
    }
}
