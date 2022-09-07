package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import org.apache.coyote.http11.FileReader;

public class HttpRequest {

    private final StartLine startLine;
    private final HttpHeader header;
    private final HttpBody body;

    private HttpRequest(final StartLine startLine, final HttpHeader header, HttpBody body) {
        this.startLine = startLine;
        this.header = header;
        this.body = body;
    }

    public static HttpRequest of(final BufferedReader bufferedReader) throws IOException {
        final StartLine startLine = StartLine.from(bufferedReader.readLine());
        final HttpHeader header = HttpHeader.from(bufferedReader);

        String h = header.getContentLength();
        final int contentLength = Integer.parseInt(h);
        final HttpBody body = HttpBody.from(bufferedReader, contentLength);
        return new HttpRequest(startLine, header, body);
    }

    public boolean isGet() {
        return startLine.isGet();
    }

    public RequestURL getRequestURL() {
        return startLine.getRequestURL();
    }

    public String getHttpBody(String key) {
        return body.get(key);
    }
}
