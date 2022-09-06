package org.apache.coyote.http11.http11request;

import java.util.Map;
import org.apache.coyote.http11.HttpMethod;

public class Http11Request {

    private final HttpMethod httpMethod;
    private final String uri;
    private final Map<String, String> header;
    private final String body;

    public Http11Request(String httpMethod, String uri, Map<String, String> header, String body) {
        this.httpMethod = HttpMethod.valueOf(httpMethod.toUpperCase());
        this.uri = uri;
        this.header = header;
        this.body = body;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getUri() {
        return uri;
    }

    public Map<String, String> getHeader() {
        return header;
    }

    public String getBody() {
        return body;
    }
}
