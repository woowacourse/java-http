package org.apache.coyote.http11.Http11Request;

public class Http11Request {

    private final String httpMethod;
    private final String uri;

    public Http11Request(String httpMethod, String uri) {
        this.httpMethod = httpMethod;
        this.uri = uri;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public String getUri() {
        return uri;
    }
}
