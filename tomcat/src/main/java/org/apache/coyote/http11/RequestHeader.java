package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RequestHeader {
    private final Map<String, String> header;

    private RequestHeader(final Map<String, String> header) {
        this.header = header;
    }

    public static RequestHeader of(final BufferedReader bufferedReader) {
        final Map<String, String> header = new HashMap<>();
        try {
            String line = bufferedReader.readLine();
            while (!"".equals(line)) {
                final String[] headerField = line.split(": ");
                header.put(headerField[0], headerField[1]);
                line = bufferedReader.readLine();
            }
            return new RequestHeader(header);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getField(final String name) {
        return header.getOrDefault(name, "text/html").split(",")[0];
    }
}
