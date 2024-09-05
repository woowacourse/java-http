package org.apache.coyote.http11.request;

import java.util.Map;
import org.apache.coyote.http11.HttpMethod;

public class RequestLine {

    private final HttpMethod method;
    private final RequestURI requestUri;
    private final String httpVersion;

    public RequestLine(HttpMethod method, RequestURI requestUri, String httpVersion) {
        this.method = method;
        this.requestUri = requestUri;
        this.httpVersion = httpVersion;
    }

    public static RequestLine from(String requestLine) {
        String[] tokens = requestLine.split(" ");
        return new RequestLine(HttpMethod.valueOf(tokens[0]), new RequestURI(tokens[1]), tokens[2]);
    }

    public HttpMethod getMethod() {
        return method;
    }

    public Map<String, String> getQueryString() {
        return requestUri.getQueryString();
    }

    public String getExtension() {
        return requestUri.getExtension();
    }

    public String getPath() {
        return requestUri.getPath();
    }

    @Override
    public String toString() {
        return String.format("%s %s %s", method, requestUri.getURI(), httpVersion);
    }
}
