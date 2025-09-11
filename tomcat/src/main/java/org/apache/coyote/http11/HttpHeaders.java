package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpHeaders {

    private final Map<String, String> headers = new HashMap<>();
    private int contentLength = 0;

    private HttpHeaders() {
    }

    public static HttpHeaders parse(BufferedReader reader) throws IOException {
        HttpHeaders result = new HttpHeaders();
        String headerLine;
        while (!(headerLine = reader.readLine()).isEmpty()) {
            int index = headerLine.indexOf(":");
            if (index != -1) {
                String key = headerLine.substring(0, index).trim();
                String value = headerLine.substring(index + 1).trim();
                result.headers.put(key, value);
                if ("Content-Length".equalsIgnoreCase(key)) {
                    result.contentLength = Integer.parseInt(value);
                }
            }
        }
        return result;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public int getContentLength() {
        return contentLength;
    }

    public String get(String key) {
        return headers.get(key);
    }
}
