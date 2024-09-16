package jakarta.http;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Header {

    private static final String PAIR_DELIMITER = ":";
    private static final String JSESSION_ID_KEY = "JSESSIONID";
    private static final int KEY_PAIR_LENGTH = 2;
    private static final int KEY_POSITION = 0;
    private static final int VALUE_POSITION = 1;

    private final Map<String, String> header = new HashMap<>();
    private final Cookie cookie;

    public static Header empty() {
        return new Header(Collections.emptyList());
    }

    public Header(List<String> header) {
        parseHeader(header);

        this.cookie = new Cookie(this.get(HttpHeaderKey.COOKIE).orElse(""));

    }

    private void parseHeader(List<String> header) {
        header.forEach(this::parsePair);
    }

    private void parsePair(String pair) {
        if (pair.contains(PAIR_DELIMITER)) {
            String[] split = pair.split(PAIR_DELIMITER);
            putIfValidPair(split);
        }
    }

    private void putIfValidPair(String[] keyValuePair) {
        if (keyValuePair.length != KEY_PAIR_LENGTH) {
            return;
        }
        String key = keyValuePair[KEY_POSITION].trim();
        String value = keyValuePair[VALUE_POSITION].trim();

        append(key, value);
    }

    public void appendJSessionId(String id) {
        append(HttpHeaderKey.SET_COOKIE, JSESSION_ID_KEY + " = " + id);
    }

    public void append(HttpHeaderKey key, String value) {
        append(key.getName(), value);
    }

    public void append(String key, String value) {
        header.put(key, value);
    }

    public Optional<String> get(HttpHeaderKey key) {
        return get(key.getName());
    }

    public Optional<String> get(String key) {
        return Optional.ofNullable(header.get(key));
    }

    public Optional<String> getJSessionId() {
        return cookie.get(JSESSION_ID_KEY);
    }

    public Map<String, String> getHeader() {
        return Collections.unmodifiableMap(header);
    }
}
