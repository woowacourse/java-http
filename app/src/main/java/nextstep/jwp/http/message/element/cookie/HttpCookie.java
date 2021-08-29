package nextstep.jwp.http.message.element.cookie;

import java.util.*;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;

public class HttpCookie implements Cookie{

    private final
    Map<String, String> values;

    public HttpCookie(String values) {
        this.values = extractCookies(values);
    }

    private Map<String, String> extractCookies(String values) {
        return Arrays.stream(values.split(";"))
                .map(value -> value.split("="))
                .filter(splitValue -> splitValue.length == 2)
                .collect(toMap(v -> v[0].trim(), v -> v[1].trim()));
    }

    public HttpCookie() {
        this(new HashMap<>());
    }

    public HttpCookie(Map<String, String> values) {
        this.values = values;
    }

    public Optional<String> get(String key) {
        return Optional.ofNullable(values.get(key));
    }

    public List<String> getKeys() {
        return new ArrayList<>(values.keySet());
    }

    public void put(String key, String value) {
        this.values.put(key, value);
    }

    public int size() {
        return values.size();
    }

    public String asString() {
        return values.entrySet().stream()
                .map(e -> String.format("%s=%s", e.getKey(), e.getValue()))
                .collect(joining(";"));
    }
}
