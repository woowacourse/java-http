package org.apache.coyote.http11.httpRequest;

import java.util.Map;

public class HttpRequest {

    private final RequestLine requestLine;
    private final Map<String, String> headers;
    private final RequestBody requestBody;

    public HttpRequest(
            final RequestLine requestLine,
            final Map<String, String> headers,
            final RequestBody requestBody
    ) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.requestBody = requestBody;
    }

    public String getPath() {
        return this.requestLine.getPath();
    }

    public Map<String, String> getParamsFromBody() {
        return this.requestBody.getParams();
    }
}
