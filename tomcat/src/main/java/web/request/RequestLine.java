package web.request;

import web.HttpMethod;

public class RequestLine {

    private final HttpMethod method;
    private final RequestUri requestUri;
    private final String httpVersion;

    public RequestLine(final String method, final String requestUri, final String httpVersion) {
        this.method = HttpMethod.valueOf(method);
        this.requestUri = new RequestUri(requestUri);
        this.httpVersion = httpVersion;
    }

    public HttpMethod getMethod() {
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
                "method=" + method +
                ", requestUri=" + requestUri +
                ", httpVersion='" + httpVersion + '\'' +
                '}';
    }
}
