package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.coyote.http11.common.QueryParser;

public class HttpRequestBody {

    private final Map<String, String> body;

    private HttpRequestBody(Map<String, String> body) {
        this.body = body;
    }

    public static HttpRequestBody of(BufferedReader bufferedReader, int contentLength) throws IOException {

        if (contentLength == 0) {
            return new HttpRequestBody(new HashMap<>());
        }

        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        String body = new String(buffer);

        return new HttpRequestBody(QueryParser.parse(body));
    }

    public Map<String, String> getBody() {
        return body;
    }
}
