package org.apache.coyote.http11.http11request;

import org.apache.coyote.http11.HttpMethod;

public class Http11Request {

    private final HttpMethod httpMethod;
    private final String uri;
    private final String body;

    public Http11Request(String httpMethod, String uri, String body) {
        this.httpMethod = HttpMethod.valueOf(httpMethod.toUpperCase());
        this.uri = uri;
        this.body = body;
        System.out.println("@@@request");
        System.out.println(httpMethod);
        System.out.println(uri);
        System.out.println(body);
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getUri() {
        return uri;
    }

    public String getBody() {
        return body;
    }
}
