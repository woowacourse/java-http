package org.apache.coyote.http11;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.coyote.view.View;

public class HttpHeaders {

    private static final String regex = "^(.+):\\s(.+)$";
    private static final Pattern pattern = Pattern.compile(regex);
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;
    private static final String DELIMITER = ": ";

    private Map<String, String> store;

    public HttpHeaders() {
        store = new LinkedHashMap<>();
    }

    public HttpHeaders(Map<String, String> store) {
        this.store = store;
    }

    public static HttpHeaders of(View view, ContentType contentType) {
        Map<String, String> store = new LinkedHashMap<>();
        store.put("Content-Type", contentType.getValue() + ";charset=utf-8");
        store.put("Content-Length", String.valueOf(view.getContent().getBytes().length));
        return new HttpHeaders(store);
    }

    public void addByString(String raw) {
        raw = raw.trim();
        Matcher matcher = pattern.matcher(raw);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("잘못된 형식의 헤더입니다. = " + raw);
        }
        String[] split = raw.split(DELIMITER);
        store.put(split[KEY_INDEX], split[VALUE_INDEX]);
    }

    public void add(String key, String value) {
        store.put(key, value);
    }

    public String findByKey(String key) {
        return store.get(key);
    }

    public List<String> getHeaders() {
        return store.entrySet().stream()
                .map(entry -> entry.getKey() + DELIMITER + entry.getValue() + " ")
                .toList();
    }

    @Override
    public String toString() {
        return "HttpHeaders{" +
                "store=" + store +
                '}';
    }
}
