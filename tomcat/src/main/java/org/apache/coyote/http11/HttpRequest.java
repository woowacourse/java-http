package org.apache.coyote.http11;

import java.io.IOException;
import java.util.Map;

public class HttpRequest {

    private final HttpRequestLine httpRequestLine;
    private final Map<HttpHeaders, String> headers;
    private final Map<String, String> bodies;

    public HttpRequest(HttpRequestLine httpRequestLine, Map<HttpHeaders, String> headers, Map<String, String> bodies) {
        this.httpRequestLine = httpRequestLine;
        this.headers = headers;
        this.bodies = bodies;
    }

    public HttpResponse<String> getHttpResponse() throws IOException {
        return httpRequestLine.getHttpResponse(bodies);
    }

    public Map<HttpHeaders, String> getHeaders() {
        return headers;
    }
}
