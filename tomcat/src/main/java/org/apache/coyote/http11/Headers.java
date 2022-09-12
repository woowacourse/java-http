package org.apache.coyote.http11;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Headers {

    private final Map<String, String> values;

    public Headers() {
        this(new LinkedHashMap<>());
    }

    private Headers(final Map<String, String> values) {
        this.values = values;
    }

    public static Headers of(final List<String> inputs) {
        Map<String, String> headers = new LinkedHashMap<>();
        for (String header : inputs.subList(1, inputs.size())) {
            if (!header.contains(": ")) {
                continue;
            }
            String[] splitHeader = header.split(": ", -1);
            headers.put(splitHeader[0], splitHeader[1]);
        }
        return new Headers(headers);
    }

    public void add(final String key, final String value) {
        this.values.put(key, value);
    }

    public String find(final String key) {
        return values.get(key);
    }

    public boolean contains(final String key) {
        return values.containsKey(key);
    }

    public Map<String, String> getValues() {
        return new LinkedHashMap<>(values);
    }

    public String getJSessionId() {
        if (values.containsKey("Cookie")) {
            HttpCookie cookie = HttpCookie.of(values.get("Cookie"));
            return cookie.find("JSESSIONID");
        }
        return null;
    }
}
