package org.apache.coyote.http11.request;

import java.util.Map;
import org.apache.coyote.http11.request.spec.HttpHeaders;
import org.apache.coyote.http11.request.spec.HttpMethod;
import org.apache.coyote.http11.request.spec.StartLine;

public class HttpRequest {

    private final StartLine startLine;
    private final HttpHeaders headers;
    private final String body;

    public HttpRequest(StartLine startLine, HttpHeaders headers) {
        this(startLine, headers, null);
    }

    public HttpRequest(StartLine startLine, HttpHeaders headers, String body) {
        this.startLine = startLine;
        this.headers = headers;
        this.body = body;
    }

    public String getPathString() {
        return startLine.getPath();
    }

    public Map<String, String> getParams() {
        return startLine.getParams();
    }

    public void setPath(String pathName) {
        startLine.setPath(pathName);
    }

    public boolean isPathEqualTo(String path) {
        return startLine.isPathEqualTo(path);
    }

    public boolean isStaticResourcePath() {
        return startLine.isStaticResourcePath();
    }

    public HttpMethod getMethod() {
        return startLine.getMethod();
    }

    public Map<String, String> getHeaders() {
        return headers.getValues();
    }

    public String getBody() {
        return body;
    }
}
