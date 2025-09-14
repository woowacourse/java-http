package org.apache.coyote.http.request;

public class RequestLine {

    private final String method;
    private final String url;
    private final String protocol;

    public RequestLine(String method, String url, String protocol) {
        this.method = method;
        this.url = url;
        this.protocol = protocol;
    }

    public String getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }
}
