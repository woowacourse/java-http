package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class FormData {

    private final Map<String, String> values;

    public FormData(String values) {
        this.values = Arrays.stream(values.split("&"))
            .map(it -> it.split("="))
            .collect(Collectors.toMap(
                keyAndValue -> keyAndValue[0],
                keyAndValue -> keyAndValue[1]));
    }

    public String getValue(String key) {
        return values.get(key);
    }
}
