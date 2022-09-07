package org.apache.coyote.http11.request;

import org.apache.coyote.http11.HttpHeader;
import org.apache.coyote.http11.HttpMethod;

public class HttpRequest {

    private final HttpRequestLine requestLine;
    private final HttpHeader headers;
    private final QueryParameter queryParameter;
    private final String body;

    private HttpRequest(final HttpRequestLine requestLine, final HttpHeader httpHeader,
                        final QueryParameter queryParameter, final String body) {
        this.requestLine = requestLine;
        this.headers = httpHeader;
        this.queryParameter = queryParameter;
        this.body = body;
    }

    public static HttpRequest of(final HttpRequestLine requestLine, final HttpHeader httpHeader,
                                 final String requestBody) {
        if (httpHeader.isFormDataType()) {
            return new HttpRequest(requestLine, httpHeader, QueryParameter.of(requestBody), "");
        }
        return new HttpRequest(requestLine, httpHeader, QueryParameter.of(requestLine.getQueryString()), requestBody);
    }

    public String getParameter(final String parameterName) {
        return queryParameter.getParameter(parameterName);
    }

    public boolean containsParameter(final String parameterName) {
        return queryParameter.contains(parameterName);
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public HttpMethod getHttpStatus() {
        return requestLine.getHttpMethod();
    }

    public String getHeader(final String header) {
        return this.headers.getHeader(header);
    }

    public String getBody() {
        return body;
    }
}
