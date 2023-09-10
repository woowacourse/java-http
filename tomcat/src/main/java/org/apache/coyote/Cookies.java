package org.apache.coyote;

import static org.apache.coyote.utils.Constant.COOKIES_DELIMITER;
import static org.apache.coyote.utils.Constant.COOKIE_DELIMITER;
import static org.apache.coyote.utils.Constant.KEY_INDEX;
import static org.apache.coyote.utils.Constant.SPLIT_LIMIT_SIZE;
import static org.apache.coyote.utils.Constant.VALUE_INDEX;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Cookies {

    private final Map<String, String> cookies;

    private Cookies(final Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public static Cookies from(final String line) {
        final Map<String, String> cookies = new HashMap<>();
        if (line == null || !line.contains(COOKIE_DELIMITER)) {
            return new Cookies(cookies);
        }

        final String[] cookieFields = line.split(COOKIES_DELIMITER);

        for (final String field : cookieFields) {
            final String[] cookie = field.split(COOKIE_DELIMITER, SPLIT_LIMIT_SIZE);
            cookies.put(cookie[KEY_INDEX], cookie[VALUE_INDEX]);
        }

        return new Cookies(cookies);
    }

    public static Cookies create(final String key, final String value) {
        final Map<String, String> cookies = new HashMap<>();
        cookies.put(key, value);

        return new Cookies(cookies);
    }

    public boolean isExist(final String name) {
        return cookies.containsKey(name);
    }

    public String getValueOf(final String name) {
        return cookies.get(name);
    }

    public String stringify() {
        return cookies.entrySet().stream()
                .map(entry -> entry.getKey() + COOKIE_DELIMITER + entry.getValue())
                .collect(Collectors.joining(COOKIES_DELIMITER));
    }
}
