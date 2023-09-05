package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.coyote.http11.common.HttpHeaders;

public class HttpRequest {

    private final RequestLine requestLine;
    private final HttpHeaders headers;
    private final String requestBody;

    public HttpRequest(final RequestLine requestLine,
                       final HttpHeaders headers,
                       final String requestBody) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.requestBody = requestBody;
    }

    public static HttpRequest parse(final BufferedReader reader) throws IOException {
        final RequestLine requestLine = RequestLine.parse(reader.readLine());
        final HttpHeaders headers = HttpHeaders.parse(readHeaders(reader));
        final String requestBody = findRequestBody(reader, headers);

        return new HttpRequest(requestLine, headers, requestBody);
    }

    private static List<String> readHeaders(final BufferedReader reader) throws IOException {
        final List<String> headers = new ArrayList<>();

        String readLine = reader.readLine();
        while (readLine != null && !readLine.isEmpty()) {
            headers.add(readLine);
            readLine = reader.readLine();
        }
        return headers;
    }

    private static String findRequestBody(final BufferedReader reader, final HttpHeaders headers)
            throws IOException {
        if (headers.hasContentLength()) {
            final int contentLength = headers.getContentLength();
            final char[] buffer = new char[contentLength];
            reader.read(buffer, 0, contentLength);
            return new String(buffer);
        }
        return "";
    }

    public String getUri() {
        return requestLine.getUri();
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public String getRequestBody() {
        return requestBody;
    }
}
