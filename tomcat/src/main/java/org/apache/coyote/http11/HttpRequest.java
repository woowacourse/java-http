package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;

public class HttpRequest {

    private final HttpRequestFirstLine firstLine;
    private final HttpRequestHeader headers;
    private final String body;

    public static HttpRequest from(final BufferedReader bufferedReader) throws IOException {
        final String firstLine = bufferedReader.readLine();
        final HttpRequestFirstLine httpRequestFirstLine = HttpRequestFirstLine.from(firstLine);

        final HttpRequestHeader headers = HttpRequestHeader.from(bufferedReader);

        final String body;
        final String nonParsedContentLength = headers.get("Content-Length");
        if (nonParsedContentLength == null) {
            body = "";
        } else {
            final int contentLength = Integer.parseInt(nonParsedContentLength);
            System.out.println("contentLength = " + contentLength);
            final char[] buffer = new char[contentLength];
            bufferedReader.read(buffer, 0, contentLength);
            body = new String(buffer);
        }

        return new HttpRequest(httpRequestFirstLine, headers, body);
    }

    private HttpRequest(
            final HttpRequestFirstLine firstLine,
            final HttpRequestHeader headers,
            final String body
    ) {
        this.firstLine = firstLine;
        this.headers = headers;
        this.body = body;
    }

    public HttpRequestFirstLine getFirstLine() {
        return firstLine;
    }

    public HttpRequestHeader getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    public String getPath() {
        return firstLine.getPath();
    }

    public String getProtocolVersion() {
        return firstLine.getProtocolVersion();
    }

}
