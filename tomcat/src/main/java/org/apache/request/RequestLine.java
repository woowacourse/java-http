package org.apache.request;

public class RequestLine {

    private String method;
    private RequestUri requestUri;
    private String httpVersion;

    public RequestLine(final String method, final String requestUri, final String httpVersion) {
        this.method = method;
        this.requestUri = new RequestUri(requestUri);
        this.httpVersion = httpVersion;
    }

    public String getMethod() {
        return method;
    }

    public RequestUri getRequestUri() {
        return requestUri;
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    @Override
    public String toString() {
        return "RequestLine{" +
                "method='" + method + '\'' +
                ", requestUri='" + requestUri.getValue() + '\'' +
                ", httpVersion='" + httpVersion + '\'' +
                '}';
    }
}
