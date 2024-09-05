package org.apache.coyote.http11.domain.request;

import java.io.IOException;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.coyote.http11.domain.HttpMethod;

public class HttpRequest {

    private final RequestLine requestLine;
    private final RequestHeaders requestHeaders;
    private final RequestBody requestBody;

//    public HttpRequest(String requestLine, List<String> headerLines, String body) throws IOException {
//        validateLineEmpty(requestLine);
//        this.requestLine = new RequestLine(requestLine);
//        this.requestHeaders = new RequestHeaders(headerLines);
//        this.requestBody = new RequestBody(body);
//    }

    public HttpRequest(RequestLine requestLine, RequestHeaders requestHeaders, RequestBody requestBody)
            throws IOException {
        this.requestLine = requestLine;
        this.requestHeaders = requestHeaders;
        this.requestBody = requestBody;
    }

    private void validateLineEmpty(String line) {
        if (StringUtils.isEmpty(line)) {
            throw new IllegalArgumentException("Line is Empty");
        }
    }

    public HttpMethod getMethod() {
        return requestLine.getMethod();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public String getHeader(String key) {
        return requestHeaders.getHeader(key);
    }

    public Map<String, String> getHeaders() {
        return requestHeaders.getHeaders();
    }

    public RequestBody getRequestBody() {
        return requestBody;
    }

    public String getParameter(String key) {
        return requestLine.getQueryParameter(key);
    }

    public Map<String, String> getParameters() {
        return requestLine.getQueryParameters();
    }

    public String getHttpVersion() {
        return requestLine.getHttpVersion();
    }
}

