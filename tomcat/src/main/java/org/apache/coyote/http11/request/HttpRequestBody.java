package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;

public class HttpRequestBody {

    private final String httpRequestBody;

    public HttpRequestBody(BufferedReader bufferedReader, HttpRequestHeaders httpRequestHeaders)
            throws IOException {
        String body = "";
        if (httpRequestHeaders.containsKey("Content-Length")) {
            int contentLength = Integer.parseInt(
                    httpRequestHeaders.get("Content-Length").split(": ")[1]);
            char[] buffer = new char[contentLength];
            bufferedReader.read(buffer, 0, contentLength);
            body = new String(buffer);
        }
        this.httpRequestBody = body;
    }

    public String getHttpRequestBody() {
        return httpRequestBody;
    }
}
