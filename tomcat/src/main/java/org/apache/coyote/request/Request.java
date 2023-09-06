package org.apache.coyote.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Request {
    private final RequestStartLine requestStartLine;
    private final RequestHeader requestHeader;
    private final RequestBody requestBody;

    public Request(RequestStartLine requestStartLine, RequestHeader requestHeader, RequestBody requestBody) {
        this.requestStartLine = requestStartLine;
        this.requestHeader = requestHeader;
        this.requestBody = requestBody;
    }

    public static Request from(final List<String> requestLines, final BufferedReader bufferedReader) throws IOException {
        final RequestStartLine startLine = RequestStartLine.from(requestLines.get(0));
        requestLines.remove(0);
        final RequestHeader requestHeader = RequestHeader.from(requestLines);
        final String readRequestBodyLine = readRequestBody(requestHeader.getContentLength(), bufferedReader);
        final RequestBody requestBody = RequestBody.from(readRequestBodyLine);

        return new Request(startLine, requestHeader, requestBody);
    }

    private static String readRequestBody(final int contentLength, final BufferedReader bufferedReader) throws IOException {
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        return new String(buffer);
    }

    public boolean isFile() {
        return true;
    }

    public boolean hasQueryString() {
        return true;
    }

    public Map<String, String> getQueryParaMap() {
        return Collections.emptyMap();
    }

    public String getFileType() {
        if (isFile()) {
            return "fileType";
        }
        return "";
    }

    public String getPath() {
        return "path";
    }

    public boolean isPost() {
        return true;
    }

    public boolean isGet() {
        return true;
    }

    public Map<String, String> getRequestBody() {
        return Collections.emptyMap();
    }

    @Override
    public String toString() {
        return requestStartLine + System.lineSeparator() +
                requestHeader + System.lineSeparator() + System.lineSeparator() +
                requestBody;
    }
}
