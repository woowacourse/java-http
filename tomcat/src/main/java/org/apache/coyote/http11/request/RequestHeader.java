package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class RequestHeader {
    private static final Pattern PATTERN = Pattern.compile("(.+?): (.+?)\r\n");

    private final Map<String, String> header;

    private RequestHeader(final Map<String, String> header) {
        this.header = header;
    }

    public static RequestHeader from(final BufferedReader bufferedReader) throws IOException {
        Map<String, String> result = new HashMap<>();
        String line = bufferedReader.readLine();
        while (!"".equals(line)) {
            final String[] split = line.split(": ", 2);
            result.put(split[0], split[1]);
            line = bufferedReader.readLine();
        }
        return new RequestHeader(result);
    }

    public String getHeaderValue(final String key) {
        return header.get(key);
    }

    public boolean containsKey(final String key) {
        return header.containsKey(key);
    }

    public Map<String, String> getHeader() {
        return header;
    }
}
