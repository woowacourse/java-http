package org.apache.coyote.http11.httpmessage.request;

import org.apache.coyote.http11.httpmessage.HttpHeader;
import org.apache.coyote.http11.httpmessage.support.HttpRequestParser;

public class HttpRequest {
    
    final HttpMethod httpMethod;
    final String path;
    final String protocol;
    final HttpHeader header;

    private HttpRequest(final HttpMethod httpMethod, final String path, final String protocol, final HttpHeader header) {
        this.httpMethod = httpMethod;
        this.path = path;
        this.protocol = protocol;
        this.header = header;
    }

    public static HttpRequest from(final String request) {
        return new HttpRequest(
            HttpRequestParser.getHttpMethod(request),
            HttpRequestParser.getPath(request),
            HttpRequestParser.getProtocol(request),
            HttpRequestParser.getHeader(request)
        );
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getPath() {
        return path;
    }
}
