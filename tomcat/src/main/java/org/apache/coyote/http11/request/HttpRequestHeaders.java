package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpRequestHeaders {

    private final Map<String, String> httpHeaders;

    public HttpRequestHeaders(BufferedReader bufferedReader) throws IOException {
        Map<String, String> headers = new HashMap<>();
        String line;
        while (!(line = bufferedReader.readLine()).equals("")) {
            String[] keyValue = line.split(": ");
            validateContainingKeyValue(keyValue);
            headers.put(keyValue[0], line.substring(keyValue.length + ": ".length() + 1));
        }
        this.httpHeaders = headers;
    }

    private void validateContainingKeyValue(String[] keyValue) {
        if (keyValue.length < 2) {
            throw new HttpFormatException();
        }
    }

    public boolean containsKey(String key) {
        return httpHeaders.containsKey(key);
    }

    public String get(String key) {
        return httpHeaders.get(key);
    }
}
