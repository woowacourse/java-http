package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

public class HttpRequest {

    private static final String CONTENT_LENGTH = "Content-Length";

    private final RequestLine requestLine;
    private final RequestHeader requestHeader;
    private final String requestBody;

    private HttpRequest(final RequestLine requestLine, final RequestHeader requestHeader, final String requestBody) {
        this.requestLine = requestLine;
        this.requestHeader = requestHeader;
        this.requestBody = requestBody;
    }

    public static HttpRequest from(final BufferedReader bufferedReader) throws IOException {
        final RequestLine requestLine = readRequestLine(bufferedReader);
        final RequestHeader requestHeader = readRequestHeader(bufferedReader);
        final String requestBody = readRequestBody(bufferedReader, requestHeader);
        return new HttpRequest(requestLine, requestHeader, requestBody);
    }

    private static RequestLine readRequestLine(final BufferedReader bufferedReader) throws IOException {
        final String line = bufferedReader.readLine();
        if (line == null) {
            return null;
        }
        return RequestLine.from(line);
    }

    private static RequestHeader readRequestHeader(final BufferedReader bufferedReader) throws IOException {
        final var lines = new ArrayList<String>();
        String line;
        while (!"".equals(line = bufferedReader.readLine())) {
            lines.add(line);
        }
        return RequestHeader.from(lines);
    }

    private static String readRequestBody(final BufferedReader bufferedReader, final RequestHeader requestHeader)
            throws IOException {
        if (requestHeader.isNotFormContentType()) {
            return null;
        }
        final var contentLength = Integer.parseInt(requestHeader.getValue(CONTENT_LENGTH));
        final var buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        return new String(buffer);
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public RequestHeader getRequestHeader() {
        return requestHeader;
    }

    public String getRequestBody() {
        return requestBody;
    }
}
