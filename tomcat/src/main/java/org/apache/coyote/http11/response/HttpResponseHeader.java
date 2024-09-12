package org.apache.coyote.http11.response;

import java.util.HashMap;
import java.util.Map;

public class HttpResponseHeader {

    private final Map<String, String> header = new HashMap<>();

    public void put(String key, String value) {
        header.put(key, value);
    }

    public String buildResponse() {
        StringBuilder response = new StringBuilder();
        header.forEach((key, value) ->
                response.append(key)
                        .append(": ")
                        .append(value)
                        .append(" \r\n"));
        return response.toString();
    }
}
