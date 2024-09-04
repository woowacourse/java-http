package org.apache.coyote.http11.header;

import util.StringUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Headers {
    private final Map<String, String> values;
    private static final String DELIMITER = ": ";

    public Headers() {
        values = new HashMap<>();
    }

    public Headers(final Map<String, String> values) {
        this.values = values;
    }

    public String get(final String name) {
        return values.getOrDefault(name, StringUtil.BLANK);
    }

    public void put(final String line) {
        final String[] arys = line.split(DELIMITER);
        values.put(arys[0], arys[1]);
    }

    public List<String> formats() {
        return values.entrySet()
                .stream()
                .map(entry -> entry.getKey() + DELIMITER + entry.getValue())
                .toList();
    }
}
