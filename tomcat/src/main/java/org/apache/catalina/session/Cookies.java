package org.apache.catalina.session;

import com.techcourse.exception.client.BadRequestException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Cookies {

    private static final String JSESSIONID = "JSESSIONID";
    private static final int COOKIE_FORMAT_LENGTH = 2;
    private static final int COOKIE_KEY_INDEX = 0;
    private static final int COOKIE_VALUE_INDEX = 1;
    private static final String DELIMITER = "=";

    private final Map<String, String> store;

    public Cookies() {
        store = new HashMap<>();
    }

    public void add(String cookies) {
        for (String cookie : cookies.split(";")) {
            String[] split = cookie.trim().split(DELIMITER);
            validateCookieFormat(split);
            store.put(split[COOKIE_KEY_INDEX], split[COOKIE_VALUE_INDEX]);
        }
    }

    private static void validateCookieFormat(String[] split) {
        if (split.length != COOKIE_FORMAT_LENGTH) {
            throw new BadRequestException("쿠키 형식이 잘못되었습니다.");
        }
    }

    public Optional<String> findCookie(String key) {
        if (store.containsKey(key)) {
            return Optional.of(store.get(key));
        }
        return Optional.empty();
    }

    public Optional<String> findSessionId() {
        return findCookie(JSESSIONID);
    }

    public static String ofJSessionId(String sessionId) {
        return JSESSIONID + DELIMITER + sessionId;
    }
}
