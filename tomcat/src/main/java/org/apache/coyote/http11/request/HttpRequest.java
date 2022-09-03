package org.apache.coyote.http11.request;

import static org.apache.catalina.utils.IOUtils.readData;
import static org.apache.coyote.http11.header.HttpHeaderType.CONTENT_LENGTH;

import java.io.BufferedReader;
import java.io.IOException;
import org.apache.coyote.http11.header.HttpHeader;

public class HttpRequest {

    private final HttpRequestStartLine startLine;
    private final HttpHeaders headers;
    private final String body;

    public HttpRequest(final HttpRequestStartLine startLine, final HttpHeaders headers, final String body) {
        this.startLine = startLine;
        this.headers = headers;
        this.body = body;
    }

    public static HttpRequest of(final BufferedReader bufferedReader) throws IOException {
        final String startLine = bufferedReader.readLine();
        if (startLine == null) {
            throw new IllegalArgumentException("request가 비어있습니다.");
        }

        final HttpRequestStartLine httpRequestStartLine = HttpRequestStartLine.of(startLine);
        final HttpHeaders headers = HttpHeaders.of(bufferedReader);
        final String body = readBody(bufferedReader, headers);

        return new HttpRequest(httpRequestStartLine, headers, body);
    }

    private static String readBody(final BufferedReader bufferedReader,
                                   final HttpHeaders headers) throws IOException {

        if (!headers.contains(CONTENT_LENGTH)) {
            return "";
        }

        final int contentLength = convertIntFromContentLength(headers.get(CONTENT_LENGTH));
        return readData(bufferedReader, contentLength);
    }

    private static int convertIntFromContentLength(final HttpHeader contentLength) {
        return Integer.parseInt(String.join("", contentLength.getValues()));
    }

    public HttpRequestStartLine getStartLine() {
        return startLine;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }
}
