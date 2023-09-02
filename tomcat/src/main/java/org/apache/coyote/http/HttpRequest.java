package org.apache.coyote.http;

public class HttpRequest {

    private final HttpHeaders httpHeaders;
    private final HttpMessage httpMessage;

    public HttpRequest(HttpHeaders httpHeaders, HttpMessage httpMessage) {
        this.httpHeaders = httpHeaders;
        this.httpMessage = httpMessage;
    }

    public String getPath() {
        return httpHeaders.getPath();
    }
}
