package org.apache.coyote.http11;


public class HttpRequest {

    private final HttpRequestMethod method;
    private final String path;
    private final String protocolVersion;
    private final HttpHeaders headers;
    private String body;

    public HttpRequest(String requestLine) {
        String[] startLineParts = requestLine.split(" ", 3);

        this.method = HttpRequestMethod.valueOf(startLineParts[0]);
        this.path = startLineParts[1];
        this.protocolVersion = startLineParts[2];
        this.headers = new HttpHeaders();
        this.body = null;
    }

    public void addHeader(String headerLine) {
        headers.add(headerLine);
    }

    public void addBody(String body) {
        this.body = body;
    }

    public HttpRequestMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    public int getContentLength() {
        return headers.findContentLength();
    }

    
    public boolean isGetMethod() {
        return method.isGet();
    }

    public boolean isPostMethod() {
        return method.isPost();
    }

    public boolean isHtmlPath() {
        return path.endsWith(".html");
    }

    @Override
    public String toString() {
        return "HttpRequest{" +
                "method=" + method +
                ", path='" + path + '\'' +
                ", protocolVersion='" + protocolVersion + '\'' +
                ", headers=" + headers +
                ", body='" + body + '\'' +
                '}';
    }
}
