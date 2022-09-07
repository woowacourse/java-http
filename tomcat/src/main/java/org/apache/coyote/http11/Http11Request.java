package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Http11Request {

    private final RequestStartLine startLine;
    private final RequestHead requestHead;
    private final String requestBody;

    private Http11Request(RequestStartLine startLine, RequestHead requestHead, String requestBody) {
        this.startLine = startLine;
        this.requestHead = requestHead;
        this.requestBody = requestBody;
    }

    public static Http11Request of(final InputStream inputStream) throws IOException {
        final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        RequestStartLine startLine = RequestStartLine.from(getStartLine(bufferedReader));
        RequestHead requestHeaders = RequestHead.of(bufferedReader);
        String requestBody = getRequestBody(bufferedReader, requestHeaders.getContentLength());
        return new Http11Request(startLine, requestHeaders, requestBody);
    }

    private static String getStartLine(final BufferedReader bufferedReader) throws IOException {
        return bufferedReader.readLine().trim();
    }

    private static String getRequestBody(BufferedReader bufferedReader, int contentLength)
            throws IOException {
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        return new String(buffer);
    }

    public boolean isGetMethod() {
        return startLine.isGetMethod();
    }

    public boolean isPostMethod() {
        return startLine.isPostMethod();
    }

    public String getRequestUrl() {
        return startLine.getRequestUrl();
    }

    public String getRequestBody() {
        return requestBody;
    }
}
