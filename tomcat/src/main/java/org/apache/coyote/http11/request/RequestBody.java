package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;

public class RequestBody {

    private final String content;

    public RequestBody(final BufferedReader bufferedReader, final RequestHeaders httpRequestHeaders) throws IOException {
        if (httpRequestHeaders.contains("Content-Length")) {
            int contentLength = Integer.parseInt(httpRequestHeaders.get("Content-Length"));
            char[] buffer = new char[contentLength];
            bufferedReader.read(buffer, 0, contentLength);
            content = new String(buffer);
            return;
        }
        content = null;
    }

    public String getContent() {
        return content;
    }
}
