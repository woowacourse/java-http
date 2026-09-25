package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;

public class RequestBody {

    private final String content;

    public RequestBody(final BufferedReader bufferedReader, final HttpHeaders httpRequestHeaders) throws IOException {
        if (httpRequestHeaders.containsKey("Content-Length")) {
            int contentLength = Integer.parseInt(httpRequestHeaders.getHeader("Content-Length"));
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
