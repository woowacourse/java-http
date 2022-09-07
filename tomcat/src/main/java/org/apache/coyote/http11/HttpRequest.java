package org.apache.coyote.http11;

import java.util.UUID;

public class HttpRequest {

    private final RequestLine requestLine;
    private final Headers headers;
    private final RequestParameters requestParameters;

    public HttpRequest(final RequestLine requestLine, final Headers headers, final String requestBody) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.requestParameters = RequestParameters.of(requestBody);
    }

    public Session getSession() {
        return new Session(UUID.randomUUID().toString());
    }

    public RequestUri getRequestUri() {
        return requestLine.getRequestUri();
    }

    public HttpMethod getMethod() {
        return requestLine.getHttpMethod();
    }

    public RequestParameters getRequestParameters() {
        if (requestLine.getHttpMethod() == HttpMethod.GET) {
            return requestLine.getRequestUri().getRequestParameters();
        }
        return requestParameters;
    }
}
