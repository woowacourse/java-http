package org.apache.coyote.http;

import java.io.BufferedReader;
import java.io.IOException;

public class HttpRequest {

    private final HttpMethod httpMethod;
    private final String url;

    public HttpRequest(String request) {
        final String[] parseRequest = request.split(" ");
        this.httpMethod = HttpMethod.from(parseRequest[0]);
        this.url = parseRequest[1];
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getUrl() {
        return url;
    }

}
