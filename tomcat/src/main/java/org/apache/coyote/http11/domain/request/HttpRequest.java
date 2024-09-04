package org.apache.coyote.http11.domain.request;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.coyote.http11.domain.HttpMethod;

public class HttpRequest {

    private final RequestLine requestLine;
    private final RequestHeaders requestHeaders;
    private final String requestMessage;

    public HttpRequest(String requestLine, List<String> headerLines, String requestMessage) throws IOException {
        validateLineEmpty(requestLine);
        this.requestLine = new RequestLine(requestLine);
        this.requestHeaders = new RequestHeaders(headerLines);
        this.requestMessage = requestMessage;
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

    public String getRequestMessage() {
        return requestMessage;
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

