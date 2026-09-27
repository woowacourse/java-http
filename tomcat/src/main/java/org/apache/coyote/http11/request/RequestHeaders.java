package org.apache.coyote.http11.request;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.TreeMap;

public class RequestHeaders {

    private final Map<String, String> headers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    public RequestHeaders(final InputStream inputStream) throws IOException {
        String line;
        while ((line = HttpInput.readLine(inputStream)) != null && !line.isEmpty()) {
            String[] header = line.split(":", 2);
            headers.put(header[0].trim(), header[1].trim());
        }
    }

    public String get(final String key) {
        return headers.get(key);
    }

    public boolean contains(String key) {
        return headers.containsKey(key);
    }
}
