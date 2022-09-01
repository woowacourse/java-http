package org.apache.coyote.http;

import java.io.BufferedReader;
import java.io.IOException;

public class HttpRequest {

    private final HttpMethod httpMethod;
    private final String url;

    public HttpRequest(final String[] request) {
        this.httpMethod = HttpMethod.from(request[0]);
        this.url = request[1];
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getUrl() {
        return url;
    }

}
