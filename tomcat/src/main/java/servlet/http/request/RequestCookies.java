package servlet.http.request;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toMap;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

public class RequestCookies {

    private static final RequestCookies EMPTY = new RequestCookies(Collections.emptyMap());
    private static final String COOKIE_DELIMITER = "; ";
    private static final String KEY_VALUE_DELIMITER = "=";
    private static final int UNLIMITED_SPLIT = -1;
    private static final int VALID_KEY_VALUE_LENGTH = 2;
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> cookies;

    private RequestCookies(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    protected static RequestCookies from(String cookies) {
        if (isEmptyCookies(cookies)) {
            return EMPTY;
        }
        return Arrays.stream(cookies.split(COOKIE_DELIMITER))
                .map(RequestCookies::splitKeyValue)
                .collect(collectingAndThen(
                        toMap(c -> c[KEY_INDEX].strip(), c -> c[VALUE_INDEX].strip()), RequestCookies::new)
                );
    }

    private static String[] splitKeyValue(String cookie) {
        String[] keyValue = cookie.split(KEY_VALUE_DELIMITER, UNLIMITED_SPLIT);
        if (keyValue.length != VALID_KEY_VALUE_LENGTH) {
            throw new IllegalArgumentException("잘못된 Cookie입니다. request cookie: '%s'".formatted(cookie));
        }
        validateNotBlankKeyValue(keyValue[KEY_INDEX], keyValue[VALUE_INDEX]);
        return keyValue;
    }

    private static void validateNotBlankKeyValue(String key, String value) {
        if (key.isBlank() || value.isBlank()) {
            throw new IllegalArgumentException("key 또는 value가 비어있습니다. key: '%s', value: '%s'".formatted(key, value));
        }
    }

    private static boolean isEmptyCookies(String cookies) {
        return cookies == null || cookies.isBlank();
    }

    protected String get(String key) {
        return cookies.get(key);
    }
}
