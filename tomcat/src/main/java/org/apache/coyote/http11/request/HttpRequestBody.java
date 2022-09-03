package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpRequestBody {

    private final Map<String, String> body;

    private HttpRequestBody(Map<String, String> body) {
        this.body = body;
    }

    public static HttpRequestBody of(BufferedReader bufferedReader, int contentLength) throws IOException {
        Map<String, String> body = new HashMap<>();

        if (contentLength == 0) {
            return new HttpRequestBody(body);
        }

        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        String bodyLines = new String(buffer);

        for (String bodyLine : bodyLines.split("&")) {
            String[] parsedBody = bodyLine.split("=");
            body.put(parsedBody[0], parsedBody[1]);
        }

        return new HttpRequestBody(body);
    }

    public Map<String, String> getBody() {
        return body;
    }
}
