package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpHeaders {

    private static final String HEADER_SEPARATOR = ":";

    private final Map<String, String> headers = new HashMap<>();
    
    public HttpHeaders(BufferedReader reader) throws IOException {
        parse(reader);
    }

    private void parse(BufferedReader reader) throws IOException {
        String line;
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            parseHeaderLine(line);
        }
    }

    private void parseHeaderLine(String line) {
        int colonIndex = line.indexOf(HEADER_SEPARATOR);
        if (colonIndex <= 0) {
            return;
        }
        String key = line.substring(0, colonIndex).trim();
        String value = line.substring(colonIndex + 1).trim();
        if (!key.isEmpty()) {
            this.put(key, value);
        }
    }

    public void put(String key, String value) {
        headers.put(key, value);
    }

    public String get(String key) {
        return headers.get(key);
    }
}
