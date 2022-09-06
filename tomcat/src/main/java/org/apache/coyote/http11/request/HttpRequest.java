package org.apache.coyote.http11.request;

import java.util.Optional;

public class HttpRequest {

    private final RequestLine requestLine;
    private final RequestHeaders requestHeaders;
    private final RequestBody requestBody;

    public HttpRequest(RequestLine requestLine, RequestHeaders requestHeaders, RequestBody requestBody) {
        this.requestLine = requestLine;
        this.requestHeaders = requestHeaders;
        this.requestBody = requestBody;
    }

    public boolean isStaticFileRequest() {
        return requestLine.getUri().getPath().contains(".");
    }

    public Optional<String> getExtension() {
        String path = requestLine.getUri().getPath();
        int index = path.indexOf(".");
        if (index > 0) {
            return Optional.of(path.substring(index + 1));
        }
        return Optional.empty();
    }

    public boolean hasRequestBody() {
        return !requestBody.isEmpty();
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public RequestHeaders getRequestHeaders() {
        return requestHeaders;
    }

    public RequestBody getRequestBody() {
        return requestBody;
    }
}
