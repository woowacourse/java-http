package com.techcourse;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyPayload {
    private final Map<String, String> value;

    public MyPayload(Map<String, String> value) {
        this.value = Collections.unmodifiableMap(value);
    }

    public static MyPayload from(String payLoad) {
        Map<String, String> result = new HashMap<>();

        for (String line : payLoad.split("&")) {
            String[] split = line.split("=");
            result.put(split[0], split[1]);
        }

        return new MyPayload(result);
    }

    public String find(String key) {
        return value.get(key);
    }

    public Map<String, String> getValue() {
        return value;
    }
}
