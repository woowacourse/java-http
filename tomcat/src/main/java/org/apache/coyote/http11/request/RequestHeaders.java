package org.apache.coyote.http11.request;

import java.util.List;
import org.apache.coyote.http11.exception.HeaderNotFoundException;

public class RequestHeaders {

    private List<String> values;

    public RequestHeaders(List<String> values) {
        this.values = values;
    }

    public String get(String key) {
        String header = values.stream()
                .filter(value -> value.contains(key))
                .findFirst()
                .orElseThrow(HeaderNotFoundException::new);
        return header.split(" ")[1];
    }

    @Override
    public String toString() {
        return "RequestHeaders{" +
                "values=" + values +
                '}';
    }
}
