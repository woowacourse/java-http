package org.apache.coyote.http11.request;

public class RequestLine {

    private final String httpMethod;
    private final String requestUrl;
    private final String version;

    public RequestLine(final String httpMethod, final String requestUrl, final String version) {
        this.httpMethod = httpMethod;
        this.requestUrl = requestUrl;
        this.version = version;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public String getVersion() {
        return version;
    }
}
