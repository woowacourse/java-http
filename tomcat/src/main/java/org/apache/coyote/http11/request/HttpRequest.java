package org.apache.coyote.http11.request;

import java.io.IOException;
import java.util.Map;
import org.apache.coyote.http11.response.HttpResponse;

public class HttpRequest {

    private final HttpRequestLine httpRequestLine;
    private final Map<String, String> headers;
    private final Map<String, String> bodies;

    public HttpRequest(HttpRequestLine httpRequestLine, Map<String, String> headers, Map<String, String> bodies) {
        this.httpRequestLine = httpRequestLine;
        this.headers = headers;
        this.bodies = bodies;
    }

    public HttpResponse<String> getHttpResponse() throws IOException {
        return httpRequestLine.getHttpResponse(bodies);
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}
