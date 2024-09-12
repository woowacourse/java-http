package org.apache.coyote.http11.request;

import java.util.Map;
import org.apache.coyote.http11.common.HttpMethod;

public class RequestLine {

    private final HttpMethod method;
    private final RequestURI requestURI;
    private final HttpVersion httpVersion;

    public RequestLine(HttpMethod method, RequestURI requestURI, HttpVersion httpVersion) {
        this.method = method;
        this.requestURI = requestURI;
        this.httpVersion = httpVersion;
    }

    public static RequestLine from(String requestLine) {
        String[] tokens = requestLine.split(" ");

        HttpMethod method = HttpMethod.from(tokens[0]);
        RequestURI requestURI = RequestURIFactory.create(tokens[1]);
        HttpVersion httpVersion = HttpVersion.from(tokens[2]);

        return new RequestLine(method, requestURI, httpVersion);
    }

    public HttpMethod getMethod() {
        return method;
    }

    public Map<String, String> getQueryString() {
        return requestURI.getQueryString();
    }

    public String getPath() {
        return requestURI.getPath();
    }

    public String getExtension() {
        return requestURI.getExtension();
    }

    public String getPathWithExtension() {
        return requestURI.getPathWithExtension();
    }

    @Override
    public String toString() {
        return String.format("%s %s %s", method, requestURI.getUri(), httpVersion.getVersion());
    }
}
