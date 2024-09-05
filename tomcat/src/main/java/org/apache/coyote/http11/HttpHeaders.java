package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpHeaders {

    private final Map<String, String> payLoads = new HashMap<>();

    public HttpHeaders(BufferedReader bufferedReader) throws IOException {
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            if (line.isBlank()) {
                break;
            }

            String[] split = line.split(":");

            if(split.length != 2) {
                continue;
            }

            String key = split[0].trim();
            String value = split[1].trim();
            payLoads.put(key, value);
        }
    }

    public Map<String, String> getPayLoads() {
        return payLoads;
    }
}
