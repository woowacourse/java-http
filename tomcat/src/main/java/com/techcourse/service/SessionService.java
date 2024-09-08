package com.techcourse.service;

import com.techcourse.model.User;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionService {

    private static final String JSESSIONID = "JSESSIONID";
    private static final Map<String, User> store = new ConcurrentHashMap<>();

    public static String createCookie(User user) {
        String uuid = UUID.randomUUID().toString();
        store.put(uuid, user);
        return JSESSIONID + "=" + uuid;
    }

    public static boolean hasSession(String cookies) {
        Optional<String> optional = Arrays.stream(cookies.split("; "))
                .filter(cookie -> cookie.startsWith(JSESSIONID + "="))
                .findAny();
        if (optional.isEmpty()) {
            return false;
        }
        String find = optional.get();
        String[] split = find.split("=");
        if (split.length != 2) {
            return false;
        }
        return store.containsKey(split[1]);
    }
}
