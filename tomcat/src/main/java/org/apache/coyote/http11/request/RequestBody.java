package org.apache.coyote.http11.request;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class RequestBody {

    private final Map<String, String> values;

    private RequestBody(Map<String, String> values) {
        this.values = values;
    }


    public static RequestBody of(String rawRequestBody) {
        if (rawRequestBody.isEmpty()) {
            return new RequestBody(new HashMap<>());
        }
        return new RequestBody(Arrays.stream(rawRequestBody.split("&"))
                .map(inputValues -> inputValues.split("="))
                .collect(Collectors.toMap(value -> value[0], value -> value[1])));
    }

    public String get(String name) {
        return this.values.get(name);
    }

    public boolean isEmpty() {
        return this.values.isEmpty();
    }
}
