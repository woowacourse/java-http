package org.apache.coyote.http11.request;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class RequestBody {

    private final String content;

    public RequestBody(final InputStream inputStream, final RequestHeaders httpRequestHeaders) throws IOException {
        if (httpRequestHeaders.contains("Content-Length")) {
            int contentLength = Integer.parseInt(httpRequestHeaders.get("Content-Length"));
            byte[] buffer = inputStream.readNBytes(contentLength);
            if (buffer.length != contentLength) {
                throw new EOFException("Request body is shorter than Content-Length");
            }
            content = new String(buffer, StandardCharsets.UTF_8);
            return;
        }
        content = null;
    }

    public String getContent() {
        return content;
    }
}
