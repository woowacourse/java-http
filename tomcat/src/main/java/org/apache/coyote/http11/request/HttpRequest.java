package org.apache.coyote.http11.request;

import java.util.Map;
import org.apache.catalina.Session;
import org.apache.coyote.http11.component.HttpMethod;
import org.apache.coyote.http11.component.HttpVersion;

public class HttpRequest {

    private final HttpRequestLine httpRequestLine;
    private final HttpRequestHeader headers;
    private final RequestBody body;

    public HttpRequest(
            HttpRequestLine httpRequestLine,
            HttpRequestHeader headers,
            RequestBody body
    ) {
        this.httpRequestLine = httpRequestLine;
        this.headers = headers;
        this.body = body;
    }

    public Session getSession(boolean needSession) {
        return headers.getSession(needSession);
    }

    public HttpMethod getHttpMethod() {
        return httpRequestLine.getHttpMethod();
    }

    public HttpVersion getHttpVersion() {
        return httpRequestLine.getHttpVersion();
    }

    public String getPath() {
        return httpRequestLine.getPath();
    }

    public Map<String, String> getQueryParams() {
        return httpRequestLine.getQueryParams();
    }

    public RequestBody getBody() {
        return body;
    }
}
