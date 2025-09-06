package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class HttpRequest {

    private final String requestMethod;
    private final String uri;

    public HttpRequest(final InputStream inputStream) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.ISO_8859_1));
        String requestLine = br.readLine();
        if (requestLine == null || requestLine.isEmpty()) {
            throw new IOException("올바르지 않은 요청입니다.");
        }
        String[] parts = requestLine.split(" ");
        if (parts.length < 2) {
            throw new IOException("올바르지 않은 요청입니다.");
        }
        this.requestMethod = parts[0];
        this.uri = parts[1];
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public String getUri() {
        return uri;
    }
}
