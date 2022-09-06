package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpHeaders {

    private final Map<String, String> values;

    private HttpHeaders(Map<String, String> values) {
        this.values = values;
    }

    public static HttpHeaders from(List<String> headers){
        final Map<String, String> values = new HashMap<>();
        for (final String header : headers) {
            final String[] split = header.split(": ");
            values.put(split[0], split[1]);
        }
        return new HttpHeaders(values);
    }

    public String getHeaderValue(String header) {
        if (!values.containsKey(header)) {
            return "0";
        }
        return values.get(header);
    }

    public Map<String, String> getValues() {
        return values;
    }
}
