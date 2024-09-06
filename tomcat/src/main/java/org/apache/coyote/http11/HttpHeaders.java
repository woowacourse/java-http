package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpHeaders {

    private final Map<String, String> header;

    public HttpHeaders(final Map<String, String> header) {
        this.header = header;
    }

    public static HttpHeaders parse(final BufferedReader bufferedReader) {
        var line = " ";
        //TODO 리팩터링
        final var httpRequestHeaders = new HashMap<String, String>();
        while (!line.isEmpty()) {
            try {
                line = bufferedReader.readLine();
            } catch (final IOException e) {
                throw new IllegalStateException("IOException 발생");
            }
            if (line == null || line.isBlank()) {
                break;
            }
            final var split = line.split(":");
            httpRequestHeaders.put(split[0].strip(), split[1].strip());
        }
        return new HttpHeaders(httpRequestHeaders);
    }

    public String get(final String key) {
        return header.get(key);
    }
}
