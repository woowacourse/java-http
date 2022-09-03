package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpRequestHeader {

    private final Map<String, String> headers;

    private HttpRequestHeader(Map<String, String> headers) {
        this.headers = headers;
    }

    public static HttpRequestHeader from(BufferedReader bufferedReader) throws IOException {
        Map<String, String> headers = new HashMap<>();

        String header = bufferedReader.readLine();
        while (!header.isBlank() && !header.isEmpty()) {
            String[] parsedHeader = header.split(": ");
            headers.put(parsedHeader[0], parsedHeader[1]);
            header = bufferedReader.readLine();
        }

        return new HttpRequestHeader(headers);
    }

    public String get(String key) {
        return headers.get(key);
    }

    public int getContentLength() {
        return Integer.parseInt(headers.getOrDefault("Content-Length", "0"));
    }
}
