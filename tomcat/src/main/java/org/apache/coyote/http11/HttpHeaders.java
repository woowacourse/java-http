package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpHeaders {

    private final Map<String, String> header;

    private HttpHeaders(final Map<String, String> header) {
        this.header = header;
    }

    public static HttpHeaders parse(final BufferedReader bufferedReader) throws IOException {
        final var httpRequestHeaders = new HashMap<String, String>();
        var line = "";
        while ((line = bufferedReader.readLine()) != null && !line.isBlank()) {
            final int index = line.indexOf(":");
            httpRequestHeaders.put(line.substring(0, index).strip(), line.substring(index + 1).strip());
        }
        return new HttpHeaders(httpRequestHeaders);
    }

    public String get(final String key) {
        return header.get(key);
    }
}
