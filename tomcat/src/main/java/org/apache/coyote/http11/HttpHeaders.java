package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpHeaders {

    private final Map<String, String> values;

    private HttpHeaders(Map<String, String> values) {
        this.values = Map.copyOf(values);
    }

    public static HttpHeaders from(BufferedReader bufferedReader) throws IOException {
        Map<String, String> headers = new LinkedHashMap<>();
        String line;
        while ((line = bufferedReader.readLine()) != null && !line.isEmpty()) {
            String[] parts = line.split(":", 2);
            if (parts.length == 2) {
                headers.put(parts[0], parts[1].trim());
            }
        }
        return new HttpHeaders(headers);
    }

    public String get(String name) {
        return values.get(name);
    }
}
