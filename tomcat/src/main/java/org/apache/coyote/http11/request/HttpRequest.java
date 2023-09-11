package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    private final HttpRequestStartLine startLine;

    private final HttpRequestHeader header;

    private final HttpRequestBody body;

    public static HttpRequest from(final BufferedReader bufferedReader) throws IOException {
        final HttpRequestStartLine startLine = extractStartLine(bufferedReader);
        final HttpRequestHeader httpRequestHeaders = extractHeader(bufferedReader);
        final HttpRequestBody httpRequestBody = extractBody(httpRequestHeaders.getContentLength(), bufferedReader);
        return new HttpRequest(startLine, httpRequestHeaders, httpRequestBody);

    }

    private static HttpRequestStartLine extractStartLine(final BufferedReader bufferedReader) throws IOException {
        return HttpRequestStartLine.from(bufferedReader.readLine());
    }

    private static HttpRequestHeader extractHeader(final BufferedReader bufferedReader)
            throws IOException {
        Map<String, String> parsedHeaders = new HashMap<>();
        String line = bufferedReader.readLine();
        while (!"".equals(line)) {
            String[] headerTokens = line.split(": ");
            parsedHeaders.put(headerTokens[0], headerTokens[1]);
            line = bufferedReader.readLine();
        }
        return new HttpRequestHeader(parsedHeaders);
    }

    private static HttpRequestBody extractBody(String contentLength, BufferedReader bufferedReader)
            throws IOException {
        if (contentLength == null) {
            return null;
        }
        int length = Integer.parseInt(contentLength.trim());
        char[] buffer = new char[length];
        bufferedReader.read(buffer, 0, length);
        return new HttpRequestBody(new String(buffer));
    }

    private HttpRequest(final HttpRequestStartLine startLine, final HttpRequestHeader header,
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
