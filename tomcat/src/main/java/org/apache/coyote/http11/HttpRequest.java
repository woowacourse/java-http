package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;

public class HttpRequest {

    private final HttpRequestFirstLine firstLine;
    private final HttpRequestHeader headers;
    private final HttpRequestBody body;

    public static HttpRequest from(final BufferedReader bufferedReader) throws IOException {
        final String firstLine = bufferedReader.readLine();
        final HttpRequestFirstLine httpRequestFirstLine = HttpRequestFirstLine.from(firstLine);
        final HttpRequestHeader headers = HttpRequestHeader.from(bufferedReader);
        final HttpRequestBody httpRequestBody = HttpRequestBody.of(bufferedReader, headers);

        return new HttpRequest(httpRequestFirstLine, headers, httpRequestBody);
    }

    private HttpRequest(
            final HttpRequestFirstLine firstLine,
            final HttpRequestHeader headers,
            final HttpRequestBody body
    ) {
        this.firstLine = firstLine;
        this.headers = headers;
        this.body = body;
    }

    public String getPath() {
        return firstLine.getHttpPath().getPath();
    }

    public String getProtocolVersion() {
        return firstLine.getProtocolVersion();
    }

}
