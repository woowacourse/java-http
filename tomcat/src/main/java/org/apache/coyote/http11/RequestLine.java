package org.apache.coyote.http11;

import java.util.Map;

public class RequestLine {

    private final String method;
    private final RequestUri requestUri;
    private final String protocol;

    private RequestLine(String method, RequestUri requestUri, String protocol) {
        this.method = method;
        this.requestUri = requestUri;
        this.protocol = protocol;
    }

    public static RequestLine of(String line) {
        String[] split = line.split(" ");
        return new RequestLine(split[0], new RequestUri(split[1]), split[2]);
    }

    public String getMethod() {
        return method;
    }

    public String getUri() {
        return requestUri.getUri();
    }

    public RequestUri getRequestUri() {
        return requestUri;
    }

    public String getProtocol() {
        return protocol;
    }

    public Map<String, String> getParams() {
        return requestUri.getQueries();
    }
}
