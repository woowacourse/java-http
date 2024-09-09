package org.apache.coyote.http11.request;

import java.util.Map;

public class HttpRequest {

    private final RequestLine requestLine;
    private final RequestHeader header;
    private final RequestBody requestBody;

    public HttpRequest(RequestLine requestLine, RequestHeader header, RequestBody requestBody) {
        this.requestLine = requestLine;
        this.header = header;
        this.requestBody = requestBody;
    }

    public String getHeader(String key) {
        return header.getHeader(key);
    }

    public String getContentType() {
        return header.getContentType();
    }

    public Map<String, String> getFormData() {
        return requestBody.getFormData();
    }

    public boolean hasBodyData() {
        return requestBody != null;
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }
}
