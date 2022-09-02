package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Headers {

    private Map<String, String> headers;

    public Headers(final BufferedReader bufferedReader) throws IOException {
        this.headers = extractHeaders(bufferedReader);
    }

    private Map<String, String> extractHeaders(final BufferedReader bufferedReader) throws IOException {
        final Map<String, String> headers = new HashMap<>();
        while (bufferedReader.ready()) {
            final String line = bufferedReader.readLine();
            if (!"".equals(line)) {
                final String[] headerValue = line.split(": ");
                final String header = headerValue[0];
                final String value = headerValue[1];
                headers.put(header, value);
            }
        }
        return headers;
    }
}
