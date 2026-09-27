package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpHeaders {

    private final Map<String, String> values;

    private HttpHeaders() {
        this.values = new LinkedHashMap<>();
    }

    private HttpHeaders(Map<String, String> values) {
        this.values = new LinkedHashMap<>(values);
    }

    public static HttpHeaders empty() {
        return new HttpHeaders();
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

    public static HttpHeaders from(Map<String, String> headers) {
        return new HttpHeaders(headers);
    }

    public void add(String name, String value) {
        values.put(name, value);
    }

    public String get(String name) {
        return values.get(name);
    }

    @Override
    public String toString() {
        return values.entrySet().stream()
                .map(header -> header.getKey() + ": " + header.getValue())
                .collect(Collectors.joining("\r\n"));
    }
}
