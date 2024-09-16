package jakarta.http;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Cookie {

    private static final String PAIR_DELIMITER = "=";
    private static final String COOKIE_DELIMITER = ";";
    private static final int KEY_PAIR_LENGTH = 2;
    private static final int KET_POSITION = 0;
    private static final int VALUE_POSITION = 1;

    private final Map<String, String> cookie = new HashMap<>();

    public Cookie(String cookie) {
        if (cookie == null) {
            cookie = "";
        }

        parseCookie(cookie);
    }

    private void parseCookie(String cookie) {
        String[] pairs = cookie.split(COOKIE_DELIMITER);

        Arrays.stream(pairs).forEach(this::parsePair);
    }

    private void parsePair(String pair) {
        if (pair.contains(PAIR_DELIMITER)) {
            String[] keyValuePair = pair.trim().split(PAIR_DELIMITER);
            putIfValidPair(keyValuePair);
        }
    }

    private void putIfValidPair(String[] keyValuePair) {
        if (keyValuePair.length != KEY_PAIR_LENGTH) {
            return;
        }
        String key = keyValuePair[KET_POSITION].trim();
        String value = keyValuePair[VALUE_POSITION].trim();

        cookie.put(key, value);
    }

    public Optional<String> get(String key) {
        return Optional.ofNullable(cookie.get(key));
    }
}
