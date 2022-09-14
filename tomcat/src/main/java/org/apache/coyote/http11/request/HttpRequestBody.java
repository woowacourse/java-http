package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpRequestBody {

    private final Map<String, String> value;

    public HttpRequestBody(final Map<String, String> value) {
        this.value = value;
    }

    public static HttpRequestBody from(
            final BufferedReader bufferedReader,
            final int contentLength
    ) throws IOException {
        if (contentLength == 0) {
            return new HttpRequestBody(new HashMap<>());
        }

        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        final String requestBody = new String(buffer);
        final Map<String, String> body = QueryParser.parsingQueryString(requestBody);
        return new HttpRequestBody(body);
    }

    public String get(final String key) {
        return value.get(key);
    }
}
