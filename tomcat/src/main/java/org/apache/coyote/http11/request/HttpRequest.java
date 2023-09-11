package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

public class HttpRequest {

    private final HttpRequestStartLine startLine;

    private final HttpRequestHeader header;

    private final HttpRequestBody body;

    public static HttpRequest from(final BufferedReader bufferedReader) throws IOException {
        final HttpRequestStartLine startLine = HttpRequestStartLine.from(bufferedReader.readLine());
        final HttpRequestHeader httpRequestHeaders = HttpRequestHeader.from(bufferedReader);
        final HttpRequestBody httpRequestBody = HttpRequestBody.from(httpRequestHeaders.getContentLength(), bufferedReader);
        return new HttpRequest(startLine, httpRequestHeaders, httpRequestBody);
    }

    private HttpRequest(final HttpRequestStartLine startLine, final HttpRequestHeader header, final HttpRequestBody body) {
        this.startLine = startLine;
        this.header = header;
        this.body = body;
    }

    public Map<String, String> parseBody() {
        return this.body.parse();
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
