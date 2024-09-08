package com.techcourse.service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CookieService {

    private static final String JSESSIONID = "JSESSIONID";
    private static final Map<String, String> store = new ConcurrentHashMap<>();

    public static String create(String account) {
        String uuid = UUID.randomUUID().toString();
        store.put(uuid, account);
        return JSESSIONID + "=" + uuid;
    }
}
