package org.apache.coyote.http11.request;

public class HttpRequest {

    private final String requestMethod;
    private final URI uri;
    private final String protocol;

    public HttpRequest(String requestLine) {
        String[] str = requestLine.split(" ");
        this.requestMethod = str[0];
        this.uri = new URI(str[1]);
        this.protocol = str[2];
    }

    public String getProtocol() {
        return protocol;
    }

    public URI getUri() {
        return uri;
    }
}
