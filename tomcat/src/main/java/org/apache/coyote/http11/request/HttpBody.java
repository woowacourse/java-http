package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpBody {

    private final Map<String, String> value;

    public HttpBody(Map<String, String> value) {
        this.value = value;
    }

    public static HttpBody from(BufferedReader bufferedReader, int contentLength) throws IOException {
        if (contentLength == 0) {
            return new HttpBody(new HashMap<>());
        }

        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        String requestBody = new String(buffer);
        Map<String, String> body = RequestURL.parsingQueryString(requestBody);
        return new HttpBody(body);
    }

    public String get(String key) {
        return value.get(key);
    }
}
