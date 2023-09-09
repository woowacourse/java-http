package org.apache.coyote.http11.response;

import java.util.LinkedHashMap;
import java.util.Map;

public class HttpResponse {

    private static final String PROTOCOL_VERSION = "HTTP/1.1";

    private StatusLine statusLine;
    private final Map<HttpResponseHeader, String> headers;
    private String body;

    private HttpResponse(final StatusLine statusLine, final Map<HttpResponseHeader, String> headers, final String body) {
        this.statusLine = statusLine;
        this.headers = headers;
        this.body = body;
    }

    public static HttpResponse create() {
        return new HttpResponse(
                new StatusLine(PROTOCOL_VERSION),
                new LinkedHashMap<>(),
                ""
        );
    }

    public void addHeader(final HttpResponseHeader header, final String value) {
        headers.put(header, value);
    }

    public String getStatusLine() {
        return statusLine.getStatusLine();
    }

    public Map<HttpResponseHeader, String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    public void setStatusCode(final StatusCode statusCode) {
        this.statusLine.setStatusCode(statusCode);
    }

    public void setBody(final String body) {
        this.body = body;
    }
}
