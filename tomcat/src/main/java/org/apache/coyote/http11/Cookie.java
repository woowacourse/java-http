package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class Cookie {

    private final Map<String, String> cookies;

    public Cookie(final String rawCookie) {
        this.cookies = parse(rawCookie);
    }

    //Set-Cookie로 내려보낼 값. 쿠키에 담기는 건 세션 아이디뿐임
    public static String ofJSessionId(final String sessionId) {
        return "JSESSIONID=" + sessionId;
    }

    private Map<String, String> parse(final String rawCookie) {
        Map<String, String> parsed = new HashMap<>();

        //Cookie 헤더가 없는 첫 방문도 정상 케이스
        if (rawCookie == null) {
            return parsed;
        }

        for (String cookie : rawCookie.split(";")) {
            String[] keyValue = cookie.trim().split("=", 2);
            if (keyValue.length != 2 || keyValue[1].isBlank()) {
                continue;
            }
            parsed.put(keyValue[0], keyValue[1].trim());
        }

        return parsed;
    }

    public boolean hasJSessionId() {
        return cookies.containsKey("JSESSIONID");
    }

    public String getJSessionId() {
        return cookies.get("JSESSIONID");
    }
}
