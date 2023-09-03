package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpRequestHeader {

    private final Map<String, String> headers;

    public static HttpRequestHeader from(final BufferedReader bufferedReader) throws IOException {
        final Map<String, String> headers = new LinkedHashMap<>();

        String line = bufferedReader.readLine();
        while (!"".equals(line)) {
            final String[] header = line.split(": ");
            headers.put(header[0], header[1]);

            line = bufferedReader.readLine();
        }

        return new HttpRequestHeader(headers);
    }

    private HttpRequestHeader(final Map<String, String> headers) {
        this.headers = headers;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String get(final String key) {
        return headers.get(key);
    }

}
