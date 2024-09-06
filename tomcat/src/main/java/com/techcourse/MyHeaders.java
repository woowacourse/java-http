package com.techcourse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyHeaders {
    private final Map<String, String> value;

    public MyHeaders(Map<String, String> value) {
        this.value = value;
    }

    public static MyHeaders from(List<String> lines) {
        Map<String, String> value = new HashMap<>();

        for (String line : lines) {
            String[] split = line.split(": ");
            value.put(split[0], split[1]);
        }

        return new MyHeaders(value);
    }

    public boolean contains(String key) {
        return value.containsKey(key);
    }

    public String find(String key) {
        return value.get(key);
    }

    public void set(String key, String value) {
        this.value.put(key, value);
    }
}
