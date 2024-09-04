package org.apache.coyote.http11.request;

public enum HttpProtocol {

    HTTP_11("HTTP/1.1");

    private final String httpMessage;

    HttpProtocol(final String httpMessage) {
        this.httpMessage = httpMessage;
    }

    public String getHttpMessage() {
        return httpMessage;
    }
}
