package org.apache.coyote.http11.response;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class ResponseHeader {

    private Map<String, String> value = new LinkedHashMap<>();

    public static ResponseHeader empty() {
        return new ResponseHeader();
    }

    public void put(String key, String value) {
        if (this.value.containsKey(key)) {
            value = this.value.get(key) + ";" + value;
        }
        this.value.put(key, value);
    }

    public String toResponseValue() {
        StringBuilder responseValue = new StringBuilder();
        for (Entry<String, String> entry : value.entrySet()) {
            responseValue.append(String.format("%s: %s ", entry.getKey(), entry.getValue()));
            responseValue.append("\r\n");
        }
        return responseValue.toString();
    }

    public String get(String key) {
        return this.value.get(key);
    }
}
