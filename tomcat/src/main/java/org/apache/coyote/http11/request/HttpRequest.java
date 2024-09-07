package org.apache.coyote.http11.request;

import java.io.IOException;
import java.util.Map;
import org.apache.coyote.http11.response.HttpResponse;

public class HttpRequest {

    private final HttpRequestLine httpRequestLine;
    private final Map<String, String> headers;
    private final RequestBody body;

    public HttpRequest(HttpRequestLine httpRequestLine, Map<String, String> headers, RequestBody body) {
        this.httpRequestLine = httpRequestLine;
        this.headers = headers;
        this.body = body;
    }

    public HttpResponse<String> getHttpResponse() throws IOException {
        return httpRequestLine.getHttpResponse(body);
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}
