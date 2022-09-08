package org.apache.coyote.request;

import java.io.BufferedReader;
import java.io.IOException;

public class HttpRequest {

    private final RequestLine requestLine;
    private final HttpHeader header;
    private final RequestBody body;

    private HttpRequest(final RequestLine requestLine, final HttpHeader header, final RequestBody body) {
        this.requestLine = requestLine;
        this.header = header;
        this.body = body;
    }

    public static HttpRequest parse(final BufferedReader bufferedReader) throws IOException {
        final RequestLine requestLine = RequestLine.parse(bufferedReader.readLine());
        final HttpHeader header = HttpHeader.parse(splitHeader(bufferedReader));
        final RequestBody body = RequestBody.parse(splitBody(bufferedReader, header));
        return new HttpRequest(requestLine, header, body);
    }

    private static String splitHeader(final BufferedReader bufferedReader) throws IOException {
        final StringBuilder lines = new StringBuilder();
        String line;
        while (!(line = bufferedReader.readLine()).equals("")) {
            lines.append(line).append("\r\n");
        }
        return lines.toString();
    }

    private static String splitBody(final BufferedReader bufferedReader, final HttpHeader header) throws IOException {
        final String contentType = header.get("Content-Type");
        final int contentLength = calculateContentLength(contentType);
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        return new String(buffer);
    }

    private static int calculateContentLength(final String contentType) {
        if (contentType == null) {
            return 0;
        }
        return Integer.parseInt(contentType);
    }

    public boolean isSameHttpMethod(final HttpMethod httpMethod) {
        return requestLine.isSameHttpMethod(httpMethod);
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public String getHeader() {
        return header.toString();
    }
}
