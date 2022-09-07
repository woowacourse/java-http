package session;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class Cookie {

    public static final String KEY_VALUE_DELIMITER = "=";
    public static final String COOKIE_DELIMITER = "; ";

    private String key;
    private String value;

    public Cookie(final String key, final String value) {
        this.key = key;
        this.value = value;
    }

    public static Map<String, String> parseCookies(final String cookieHeaderValue) {
        return Arrays.stream(cookieHeaderValue.split(COOKIE_DELIMITER))
                .map(i -> i.split(KEY_VALUE_DELIMITER))
                .collect(Collectors.toMap(i -> i[0], i -> i[1]));
    }

    public String toStringForHeader() {
        return key + KEY_VALUE_DELIMITER + value;
    }

//    public String getKey() {
//        return key;
//    }
//
//    public String getValue() {
//        return value;
//    }
}
