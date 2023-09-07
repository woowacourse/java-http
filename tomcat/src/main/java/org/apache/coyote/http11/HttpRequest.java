package org.apache.coyote.http11;

public class HttpRequest {

    private final RequestStartLine startLine;

    private final RequestHeader header;

    private final RequestBody body;

    public HttpRequest(final RequestStartLine startLine, final RequestHeader header,
                       final RequestBody body) {
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

    public RequestBody getBody() {
        return this.body;
    }

}
