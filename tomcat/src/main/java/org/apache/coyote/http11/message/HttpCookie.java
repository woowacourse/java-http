package org.apache.coyote.http11.message;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

//TODO: 요청 파싱용 쿠키와 응답 생성용 쿠키 분리  (2025-09-9, 화, 21:8)
// https://github.com/woowacourse/java-http/pull/899#discussion_r2331128275
public class HttpCookie {
    public static final String JSESSIONID = "JSESSIONID";
    private final Map<String, String> cookies = new HashMap<>();

    public HttpCookie() {
    }

    private HttpCookie(Map<String, String> cookies) {
        this.cookies.putAll(cookies);
    }

    public static HttpCookie from(HttpHeaders header) {
        Map<String, String> cookies = parseHeader(header);
        return new HttpCookie(cookies);
    }

    public String get(String name) {
        return cookies.get(name);
    }

    public void addJSessionId(String value) {
        cookies.put(JSESSIONID, value);
    }

    public boolean hasJSessionId() {
        return cookies.containsKey(JSESSIONID);
    }

    public String getJsessionid() {
        return cookies.get(JSESSIONID);
    }

    private static Map<String, String> parseHeader(HttpHeaders header) {
        Map<String, String> cookies = new HashMap<>();
        List<String> cookieStrings = header.get("Cookie");
        if (cookieStrings == null || cookieStrings.isEmpty()) {
            return cookies;
        }

        for (String cookieString : cookieStrings) {
            String[] pairs = cookieString.split(";");
            for (String pair : pairs) {
                addPairToCookies(pair, cookies);
            }
        }

        return cookies;
    }

    private static void addPairToCookies(String pair, Map<String, String> cookies) {
        int idx = pair.indexOf('=');
        if (idx != -1) {
            String key = pair.substring(0, idx).trim();
            String value = pair.substring(idx + 1).trim();
            cookies.put(key, value);
        }
    }

    public String toHeaderString() {
        StringJoiner joiner = new StringJoiner("; ");
        cookies.forEach((key, value) -> joiner.add(key + "=" + value));
        return joiner.toString();
    }
}
