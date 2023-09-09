package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.coyote.http11.cookie.HttpCookie;

public class Headers {
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;
    private static final String HEADER_DELIMITER = ": ";

    private final Map<String, String> values;

    public static Headers empty(){
        return new Headers(new HashMap<>());
    }

    private Headers(Map<String, String> headers) {
        this.values = headers;
    }

    public static Headers from(List<String> headers) {
        Map<String, String> values = new HashMap<>();
        for (String header : headers) {
            String[] keyAndValue = header.split(HEADER_DELIMITER);
            values.put(keyAndValue[KEY_INDEX], keyAndValue[VALUE_INDEX]);
        }
        return new Headers(values);
    }

    public void add(String key, String value) {
        this.values.put(key, value);
    }

    public void setCookie(HttpCookie httpCookie) {
        String setCookieValue = httpCookie.names()
                .stream()
                .map(name -> name + "=" + httpCookie.getValue(name))
                .collect(Collectors.joining(";"));

        values.put("Set-Cookie", setCookieValue);
    }

    public boolean containsKey(String key) {
        return values.containsKey(key);
    }

    public Map<String, String> getValues() {
        return values;
    }

    public String getValue(String key) {
        return values.getOrDefault(key, "");
    }

}
