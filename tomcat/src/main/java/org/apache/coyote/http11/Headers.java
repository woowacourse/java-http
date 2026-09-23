package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Headers {

    private final Map<String, String> headers;

    public Headers(Map<String, String> headers) {
        this.headers = Collections.unmodifiableMap(headers);
    }

    public static Headers of(BufferedReader reader) throws IOException {
        Map<String, String> headers = new HashMap<>();
        String line;
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            String[] parts = line.split(":", 2);
            String key = parts[0].toLowerCase();
            String value = parts[1];
            headers.put(key, value);
        }
        return new Headers(headers);
    }

    public String getValue(String key) {
        return headers.getOrDefault(key, "");
    }
}
