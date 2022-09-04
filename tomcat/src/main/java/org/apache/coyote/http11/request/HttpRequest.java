package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

public class HttpRequest {

    private final HttpRequestStartLine startLine;
    private final HttpRequestHeader headers;
    private final HttpRequestBody body;

    private HttpRequest(HttpRequestStartLine startLine, HttpRequestHeader headers, HttpRequestBody body) {
        this.startLine = startLine;
        this.headers = headers;
        this.body = body;
    }

    public static HttpRequest from(BufferedReader bufferedReader) throws IOException {
        HttpRequestStartLine startLine = HttpRequestStartLine.from(bufferedReader.readLine());

        HttpRequestHeader headers = HttpRequestHeader.from(bufferedReader);

        int contentLength = headers.getContentLength();
        HttpRequestBody body = HttpRequestBody.of(bufferedReader, contentLength);

        return new HttpRequest(startLine, headers, body);
    }

    public boolean isGet() {
        return "GET".equals(startLine.getMethod());
    }

    public boolean isPost() {
        return "POST".equals(startLine.getMethod());
    }

    public String getUri() {
        return startLine.getUri();
    }

    public boolean containsSession() {
        return headers.containsSession();
    }

    public String getSessionFromCookie() {
        return headers.getSession();
    }

    public Map<String, String> getBody() {
        return body.getBody();
    }
}
