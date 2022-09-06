package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

public class HttpRequest {

    private final RequestStartLine startLine;
    private final RequestHeader header;
    private final RequestBody body;

    public HttpRequest(RequestStartLine startLine, RequestHeader header, RequestBody body) {
        this.startLine = startLine;
        this.header = header;
        this.body = body;
    }

    public static HttpRequest read(BufferedReader bufferedReader) throws IOException {
        RequestStartLine startLine = RequestStartLine.from(bufferedReader.readLine());
        RequestHeader header = RequestHeader.parse(bufferedReader);
        RequestBody body = RequestBody.from(bufferedReader, header.getContentLength());

        return new HttpRequest(startLine, header, body);
    }

    public boolean isGet() {
        return startLine.isGet();
    }

    public boolean isPost() {
        return startLine.isPost();
    }

    public String getUrl() {
        return startLine.getPath().getUrl();
    }

    public Map<String, String> getParams() {
        return startLine.getPath().getRequestParams();
    }

    public Map<String, String> getBody() {
        return body.getRequestBody();
    }
}
