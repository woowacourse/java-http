package org.apache.coyote.http11.http11request;

import org.apache.coyote.http11.HttpMethod;

public class Http11Request {

    private final HttpMethod httpMethod;
    private final String uri;

    public Http11Request(String httpMethod, String uri) {
        this.httpMethod = HttpMethod.valueOf(httpMethod.toUpperCase());
        this.uri = uri;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getUri() {
        return uri;
    }
}
