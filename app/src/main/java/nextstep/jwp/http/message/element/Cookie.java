package nextstep.jwp.http.message.element;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.toMap;

public class Cookie {

    private final
    Map<String, String> values;

    public Cookie(String values) {
        this.values = extractCookies(values);
    }

    private Map<String, String> extractCookies(String values) {
        return Arrays.stream(values.split(";"))
                .map(value -> value.split("="))
                .filter(splitValue -> splitValue.length == 2)
                .collect(toMap(v -> v[0].trim(), v -> v[1].trim()));
    }

    public Cookie() {
        this(new HashMap<>());
    }

    public Cookie(Map<String, String> values) {
        this.values = values;
    }

    public Optional<String> get(String key) {
        return Optional.ofNullable(values.get(key));
    }

    public int size() {
        return values.size();
    }
}
