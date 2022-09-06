package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpHeaders {
    final Map<String, String> header;

    public HttpHeaders(Map<String, String> header) {
        this.header = header;
    }

    public static HttpHeaders create(BufferedReader bufferedReader) throws IOException {
        Map<String, String> mp = new LinkedHashMap<>();
        while (bufferedReader.ready()) {
            String line = bufferedReader.readLine();
            if (line.isEmpty()) {
                break;
            }
            String[] header = line.split(": ");
            mp.put(header[0], header[1]);
        }
        return new HttpHeaders(mp);
    }

    public String get(String key) {
        return header.get(key);
    }

    public Map<String, String> getHeader() {
        return header;
    }
}
