package org.apache.coyote.http11;

import com.techcourse.exception.client.BadRequestException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.catalina.session.Cookies;
import org.apache.coyote.view.View;

public class HttpHeaders {

    private static final String regex = "^(.+):\\s(.+)$";
    private static final Pattern pattern = Pattern.compile(regex);
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;
    private static final String DELIMITER = ": ";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String UTF_8 = ";charset=utf-8";
    private static final String COOKIE = "Cookie";

    private final Map<String, String> store;
    private final Cookies cookies;

    public HttpHeaders() {
        store = new LinkedHashMap<>();
        cookies = new Cookies();
    }

    public HttpHeaders(Map<String, String> store) {
        this.store = store;
        cookies = new Cookies();
    }

    public static HttpHeaders create(HttpRequest request, HttpResponse response) {
        return create(response.getView(), ContentType.findByPath(request.getPath()));
    }

    public static HttpHeaders create(View view, ContentType contentType) {
        Map<String, String> store = new LinkedHashMap<>();
        if (view != null && contentType != null) {
            store.put(CONTENT_TYPE, contentType.getValue() + UTF_8);
            store.put(CONTENT_LENGTH, String.valueOf(view.getContent().getBytes().length));
        }
        return new HttpHeaders(store);
    }

    public void addByString(String raw) {
        raw = raw.trim();
        Matcher matcher = pattern.matcher(raw);
        if (!matcher.matches()) {
            throw new BadRequestException("잘못된 형식의 헤더입니다. = " + raw);
        }
        String[] split = raw.split(DELIMITER);
        if (split[KEY_INDEX].equals(COOKIE)) {
            handleCookie(split[VALUE_INDEX]);
        }
        store.put(split[KEY_INDEX], split[VALUE_INDEX]);
    }

    private void handleCookie(String cookie) {
        cookies.add(cookie);
    }

    public void add(String key, String value) {
        store.put(key, value);
    }

    public Optional<String> findByKey(String key) {
        String value = store.get(key);
        return Optional.ofNullable(value);
    }

    public Optional<String> findSessionId() {
        return cookies.findSessionId();
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
