package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Http11Request {

    private final RequestStartLine startLine;
    private final RequestHeaders requestHeaders;
    private final String requestBody;

    private Http11Request(RequestStartLine startLine, RequestHeaders requestHeaders, String requestBody) {
        this.startLine = startLine;
        this.requestHeaders = requestHeaders;
        this.requestBody = requestBody;
    }

    public static Http11Request from(final InputStream inputStream) throws IOException {
        final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        RequestStartLine startLine = RequestStartLine.from(getStartLine(bufferedReader));
        RequestHeaders requestHeaders = RequestHeaders.from(bufferedReader);
        String requestBody = getRequestBody(bufferedReader, requestHeaders.getContentLength());
        return new Http11Request(startLine, requestHeaders, requestBody);
    }

    private static String getStartLine(final BufferedReader bufferedReader) throws IOException {
        return bufferedReader.readLine().trim();
    }

    private static String getRequestBody(final BufferedReader bufferedReader, final int contentLength)
            throws IOException {
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        return new String(buffer);
    }

    public boolean hasNoJsessionIdCookie() {
        return requestHeaders.hasNoCookieNamed("JSESSIONID");
    }

    public boolean isGetMethod() {
        return startLine.isGetMethod();
    }

    public boolean isPostMethod() {
        return startLine.isPostMethod();
    }

    public String getRequestUrl() {
        return startLine.getPath();
    }

    public String getRequestBody() {
        return requestBody;
    }
}
