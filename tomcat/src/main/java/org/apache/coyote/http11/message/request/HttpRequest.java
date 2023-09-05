package org.apache.coyote.http11.message.request;


import org.apache.coyote.http11.message.Headers;
import org.apache.coyote.http11.message.HttpMethod;
import org.apache.coyote.http11.message.HttpVersion;

public class HttpRequest {

    private final HttpMethod method;
    private final HttpVersion httpVersion;
    private final RequestURI requestURI;
    private final Headers headers;
    private final RequestBody requestBody;

    private HttpRequest(HttpMethod method, HttpVersion httpVersion,
                        RequestURI requestURI, Headers headers, RequestBody requestBody) {
        this.method = method;
        this.httpVersion = httpVersion;
        this.requestURI = requestURI;
        this.headers = headers;
        this.requestBody = requestBody;
    }

    public static HttpRequest from(String startLine, Headers headers, RequestBody body) {
        String[] splitStartLine = startLine.split(" ");
        String httpMethod = splitStartLine[0];
        String requestURI = splitStartLine[1];
        String httpVersion = splitStartLine[2];

        return new HttpRequest(HttpMethod.from(httpMethod), HttpVersion.from(httpVersion),
                RequestURI.from(requestURI), headers, body);
    }

    public HttpMethod getMethod() {
        return method;
    }

    public HttpVersion getHttpVersion() {
        return httpVersion;
    }

    public RequestURI getRequestURI() {
        return requestURI;
    }

    public Headers getHeaders() {
        return headers;
    }

    public RequestBody getBody() {
        return requestBody;
    }
}
