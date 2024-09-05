package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpHeaders {

    private final Map<String, String> payLoads;

    public HttpHeaders(Map<String, String> payLoads) {
        this.payLoads = payLoads;
    }

    public static HttpHeaders readRequestHeader(BufferedReader bufferedReader) throws IOException {
        Map<String, String> payLoads = new HashMap<>();
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

        return new HttpHeaders(payLoads);
    }

    public Map<String, String> getPayLoads() {
        return payLoads;
    }
}
