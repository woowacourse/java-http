package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class Headers {

    private final Map<String, String> headers = new HashMap<>();

    public Headers(Map<String, String> headers) {
        this.headers.putAll(headers);
    }

    public static Headers of(InputStream inputStream) throws IOException {
        Map<String, String> headers = new HashMap<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            String[] parts = line.split(":", 2);
            String key = parts[0].toLowerCase();
            String value = parts[1];
            headers.put(key, value);
        }
        return new Headers(headers);
    }

    public Map<String, String> getHeaders() {
        return Map.copyOf(headers);
    }
}
