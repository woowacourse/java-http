package org.apache.coyote.http11;

public class HttpRequest {

    private final HttpRequestHeader httpRequestHeader;
    private HttpRequestBody httpRequestBody;

    public HttpRequest(HttpRequestHeader httpRequestHeader, HttpRequestBody httpRequestBody) {
        this.httpRequestHeader = httpRequestHeader;
        this.httpRequestBody = httpRequestBody;
    }

    public HttpRequest(HttpRequestHeader httpRequestHeader) {
        this(httpRequestHeader, null);
    }

    public boolean isMethod(String name) {
        return httpRequestHeader.isMethod(name);
    }

    public boolean isPath(String path) {
        return httpRequestHeader.isPath(path);
    }

    public boolean containsKey(String key) {
        return httpRequestHeader.containsKey(key);
    }

    public String getValue(String key) {
        return httpRequestHeader.getValue(key);
    }

    public HttpMethod getMethod() {
        return httpRequestHeader.getMethod();
    }

    public String getPath() {
        return httpRequestHeader.getPath();
    }

    public String getVersion() {
        return httpRequestHeader.getVersion();
    }

    public String getBody() {
        return httpRequestBody.getBody();
    }

    public HttpRequestHeader getHttpRequestHeader() {
        return httpRequestHeader;
    }

    public HttpRequestBody getHttpRequestBody() {
        return httpRequestBody;
    }

    @Override
    public String toString() {
        return "HttpRequest{\n" +
                "httpRequestHeader=" + httpRequestHeader +
                ",\n httpRequestBody=" + httpRequestBody +
                "\n}";
    }
}
