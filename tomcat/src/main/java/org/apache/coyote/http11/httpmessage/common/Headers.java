package org.apache.coyote.http11.httpmessage.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Headers {

    final Map<String, String> headers;

    public Headers() {
        this.headers = new LinkedHashMap<>();
    }

    public Headers(Map<String, String> headers) {
        this.headers = headers;
    }

    public Headers(BufferedReader bufferedReader) throws IOException {
        headers = new HashMap<>();
        while (true) {
            String buffer = bufferedReader.readLine();
            if (buffer == null || buffer.length() == 0) {
                break;
            }
            String key = buffer.split(": ")[0];
            String value = buffer.split(": ")[1];
            headers.put(key, value);
        }

    }

    public Headers add(final String fieldName, final String fieldValue) {
        Map<String, String> newHeaders = new LinkedHashMap<>(headers);
        newHeaders.put(fieldName, fieldValue);

        return new Headers(newHeaders);
    }

    public String parseToString() {
        return headers.entrySet()
                .stream()
                .map(it -> it.getKey() + ": " + it.getValue() + " ")
                .collect(Collectors.joining("\r\n"));
    }
}
