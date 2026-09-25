package org.apache.coyote.http11.request;

import org.apache.coyote.http11.HttpCookie;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

public class HttpHeaders {

    private final Map<String, String> headers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    public HttpHeaders(final BufferedReader bufferedReader) throws IOException {
        String line;
        while ((line = bufferedReader.readLine()) != null && !line.isEmpty()) {
            String[] header = line.split(":", 2);
            headers.put(header[0].trim(), header[1].trim());
        }
    }

    public String getHeader(final String key) {
        return headers.get(key);
    }

    public boolean containsKey(String key) {
        return headers.containsKey(key);
    }
}
