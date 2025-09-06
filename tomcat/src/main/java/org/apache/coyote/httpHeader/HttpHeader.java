package org.apache.coyote.httpHeader;

import java.util.List;
import java.util.Map;

public class HttpHeader {

    private final RequestLine requestLine;
    private final Map<String, String> headers;

    public HttpHeader(final String requestLine, final List<String> headers) {
        this.requestLine = new RequestLine(requestLine);
        this.headers = HttpHeaderParser.getHeaders(headers);
    }

    public HttpMethod getHttpMethod() {
        return requestLine.getHttpMethod();
    }

    public String getPurePath() {
        return requestLine.getPurePath();
    }

    public Map<String, String> getQueries() {
        return requestLine.getQueryValues();
    }
}
