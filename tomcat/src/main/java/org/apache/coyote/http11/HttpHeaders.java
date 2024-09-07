package org.apache.coyote.http11;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpHeaders {

    private static final String regex = "^(.+):\\s(.+)$";
    private static final Pattern pattern = Pattern.compile(regex);
    public static final int KEY_INDEX = 0;
    public static final int VALUE_INDEX = 1;

    private Map<String, String> store;

    public HttpHeaders() {
        store = new LinkedHashMap<>();
    }

    public HttpHeaders(Map<String, String> store) {
        this.store = store;
    }

    public void addByString(String raw) {
        raw = raw.trim();
        Matcher matcher = pattern.matcher(raw);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("잘못된 형식의 헤더입니다. = " + raw);
        }
        String[] split = raw.split(": ");
        store.put(split[KEY_INDEX], split[VALUE_INDEX]);
    }

    public void add(String key, String value) {
        store.put(key, value);
    }

    public String findByKey(String key) {
        return store.get(key);
    }

    @Override
    public String toString() {
        return "HttpHeaders{" +
                "store=" + store +
                '}';
    }
}
