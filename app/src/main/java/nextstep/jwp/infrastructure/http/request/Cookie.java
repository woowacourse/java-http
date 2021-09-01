package nextstep.jwp.infrastructure.http.request;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class Cookie {

    private static final String DELIMITER = ";";
    private static final String KEY_AND_VALUE_DELIMITER = "=";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> elements;

    public Cookie() {
        this(new HashMap<>());
    }

    public Cookie(final Map<String, String> elements) {
        this.elements = elements;
    }

    public static Cookie of(final String value) {

        final Map<String, String> elements = parse(value);

        return new Cookie(elements);
    }

    private static Map<String, String> parse(final String value) {
        return Arrays.stream(value.split(DELIMITER))
            .map(String::trim)
            .map((keyAndValue) -> {
                final String[] split = keyAndValue.split(KEY_AND_VALUE_DELIMITER, 2);
                validateKeyAndValue(split);
                return split;
            })
            .collect(Collectors.toMap(split -> split[KEY_INDEX], split -> split[VALUE_INDEX]));
    }

    private static void validateKeyAndValue(final String[] keyAndValue) {
        if (keyAndValue.length != 2 || "".equals(keyAndValue[KEY_INDEX])) {
            throw new IllegalArgumentException("Invalid format.");
        }
    }

    public String getValue(final String key) {
        if (!hasKey(key)) {
            throw new IllegalArgumentException(String.format("Cannot find key.(%s)", key));
        }
        return elements.get(key);
    }

    public boolean hasKey(final String key) {
        return elements.containsKey(key);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Cookie cookie = (Cookie) o;
        return Objects.equals(elements, cookie.elements);
    }

    @Override
    public int hashCode() {
        return Objects.hash(elements);
    }
}
