package session;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Session {

    public static final String COOKIE_DELIMITER = "; ";
    private Map<String, Cookie> cookies = new HashMap<>();

    public void addCookie(final String key, final String value) {
        cookies.put(key, new Cookie(key, value));
    }

//    public Optional<String> findCookie(final String key) {
//        if (!cookies.containsKey(key)) {
//            return Optional.empty();
//        }
//        return Optional.of(cookies.get(key));
//    }

    public String findAllCookies() {
        return cookies.keySet().stream()
                .map(i -> cookies.get(i).toStringForHeader())
                .collect(Collectors.joining(COOKIE_DELIMITER));
    }
}
