package org.apache.coyote.http11.common;

import java.util.HashMap;
import java.util.Map;

public class Cookies {

    public static final String HEADER_NAME = "Cookie: ";
    private final Map<String, String> data = new HashMap<>();

    public static Cookies from(String header) {
        for (var line : header.split("\r\n")) {
            if (line.startsWith(HEADER_NAME)) {
                line = line.substring(HEADER_NAME.length());
                Cookies cookies = new Cookies();
                String[] originalCookies = line.split("; ");
                for (var originalCookie : originalCookies) {
                    String[] keyAndValue = originalCookie.split("=");
                    cookies.put(keyAndValue[0], keyAndValue[1]);
                }
                return cookies;
            }
        }
        return new Cookies();
    }

    public void put(String key, String value) {
        if (data.containsKey(key)) {
            throw new IllegalArgumentException("중복된 쿼리 파라미터가 전달되었습니다.");
        }
        data.put(key, value);
    }

    public String get(String key) {
        return data.get(key);
    }

    @Override
    public String toString() {
        if (data.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (var set : data.entrySet()) {
            sb.append("Set-Cookie: ").append(set.getKey()).append("=").append(set.getValue());
        }
        return sb.toString();
    }
}
