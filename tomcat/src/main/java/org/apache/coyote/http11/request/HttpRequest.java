package org.apache.coyote.http11.request;

public class HttpRequest {

    private final HttpRequestStartLine startLine;

    private final HttpRequestHeader header;

    private final HttpRequestBody body;

    public HttpRequest(final HttpRequestStartLine startLine, final HttpRequestHeader header,
                       final HttpRequestBody body) {
        this.startLine = startLine;
        this.header = header;
        this.body = body;

    }

    public boolean isPOST() {
        return this.startLine.isPOST();
    }

    public boolean isGET() {
        return this.startLine.isGET();
    }

    public boolean isNotExistBody() {
        return this.body == null;
    }

    public boolean isSamePath(String path) {
        return this.startLine.isSamePath(path);
    }

    public String getAccept() {
        return this.header.getAccept();
    }

    public HttpCookie getCookie() {
        return this.header.getCookie();
    }

    public String getPath() {
        return this.startLine.getPath();
    }

    public HttpRequestBody getBody() {
        return this.body;
    }

}
