package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpRequestHeader {

    private final Map<String, String> header = new HashMap<>();

    public HttpRequestHeader(BufferedReader reader) throws IOException {
        String readLine;
        while (!"".equals(readLine = reader.readLine())) {
            String[] split = readLine.split(": ");
            header.put(split[0], split[1]);
        }
    }

    public String get(String key) {
        return header.get(key);
    }

    public String getOrDefault(String key, String defaultValue) {
        return header.getOrDefault(key, defaultValue);
    }
}
