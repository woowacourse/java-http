package org.apache.coyote.http11.cookie;

import util.BiValue;
import util.StringUtil;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class Cookies {
    private static final String DELIMITER = "; ";
    private static final String COOKIE_DELIMITER = "=";
    public static final String SESSION_ID = "JSESSIONID";

    private final Map<String, String> values;

    public static Cookies from(final String line) {
        if (line == null || line.isEmpty()) {
            return new Cookies(new HashMap<>());
        }

        final Cookies cookies = new Cookies(Arrays.stream(line.split(DELIMITER))
                .map(s -> StringUtil.splitBiValue(s, COOKIE_DELIMITER))
                .filter(BiValue::secondNotNull)
                .collect(Collectors.toMap(BiValue::first, BiValue::second)));

        validateCookieFormat(cookies);
        return cookies;
    }

    private static void validateCookieFormat(final Cookies cookies) {

        if (StringUtil.filterBlank(cookies.values.values())) {
            throw new IllegalArgumentException("쿠키 생성 부분에서 문제가 생겼습니다.");
        }
    }

    public Cookies(final Map<String, String> values) {
        this.values = values;
    }

    public String getCookie(final String name) {
        return StringUtil.blankIfNull(values.get(name));
    }

    @Override
    public String toString() {
        return "Cookies{\n" +
                values.entrySet()
                        .stream()
                        .map(entry -> "  " + entry.getKey() + ": " + entry.getValue())
                        .collect(Collectors.joining(",\n")) +
                "\n}";
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof final Cookies cookies)) return false;
        return Objects.equals(values, cookies.values);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(values);
    }
}
