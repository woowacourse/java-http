package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RequestHeaderManager {

    private String method;
    private String path;
    private String version;
    private final Map<String, String> headers = new HashMap<>();

    public void read(BufferedReader br) throws IOException {
        String requestLine = br.readLine();
        if (requestLine == null || requestLine.isEmpty()) {
            throw new IllegalArgumentException("request is Empty");
        }

        String[] parts = requestLine.split(" ", 3);
        if (parts.length < 3) {
            throw new IllegalArgumentException();
        }

        method = parts[0];
        path = parts[1];
        version = parts[2];

        String line;
        while ((line = br.readLine()) != null && !line.isEmpty()) {
            int idx = line.indexOf(':');
            if (idx <= 0) {
                throw new IllegalArgumentException();
            }
            String name = line.substring(0, idx).trim();
            String value = line.substring(idx + 1).trim();
            headers.put(name, value);
        }
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getVersion() {
        return version;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getHeader(String name) {
        return headers.get(name);
    }

    public boolean equalsPath(String path) {
        return this.path.equals(path);
    }
}
