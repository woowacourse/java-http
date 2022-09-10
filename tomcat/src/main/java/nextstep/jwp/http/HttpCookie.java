package nextstep.jwp.http;

import nextstep.jwp.exception.InvalidException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpCookie {

    private static final String VALUE_DELIMITER = "; ";
    private static final String KEY_VALUE_DELIMITER = "=";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;
    private static final int KEY_VALUE_SIZE = 2;

    private final Map<String, String> values;

    private HttpCookie(final Map<String, String> values) {
        this.values = values;
    }

    public static HttpCookie create() {
        return new HttpCookie(new LinkedHashMap<>());
    }

    public static HttpCookie create(final String rawCookie) {
        Map<String, String> value = new LinkedHashMap<>();
        if (rawCookie != null) {
            putKeyValue(rawCookie, value);
        }
        return new HttpCookie(value);
    }

    private static void putKeyValue(final String rawCookie, final Map<String, String> values) {
        for (String keyValue : rawCookie.split(VALUE_DELIMITER)) {
            final String[] seperatedKeyValue = keyValue.split(KEY_VALUE_DELIMITER);
            validateKeyValueSize(seperatedKeyValue);
            values.put(seperatedKeyValue[KEY_INDEX], seperatedKeyValue[VALUE_INDEX]);
        }
    }

    private static void validateKeyValueSize(final String[] seperatedKeyValue) {
        if (seperatedKeyValue.length != KEY_VALUE_SIZE) {
            throw new InvalidException("올바른 쿠키 형태가 아닙니다.");
        }
    }

    public void put(final String key, final String value) {
        this.values.put(key, value);
    }

    public String findByKey(final String key) {
        return values.get(key);
    }

    public String parse() {
        final List<String> collect = values.entrySet()
                .stream()
                .map(entry -> entry.getKey() + KEY_VALUE_DELIMITER + entry.getValue())
                .collect(Collectors.toList());
        return String.join(VALUE_DELIMITER, collect);
    }
}
