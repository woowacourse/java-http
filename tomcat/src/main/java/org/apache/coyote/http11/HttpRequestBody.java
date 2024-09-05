package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;

public class HttpRequestBody {

    private final String body;

    public HttpRequestBody(int contentLength, BufferedReader bufferedReader) {
        try {
            char[] buffer = new char[contentLength];
            bufferedReader.read(buffer, 0, contentLength);
            this.body = new String(buffer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "HttpRequestBody{\n" +
                "body='" + body + '\'' +
                "\n}";
    }
}
