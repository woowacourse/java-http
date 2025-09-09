package org.apache.coyote.http11;

import java.util.Map;
import java.util.UUID;

public class HttpCookies {

    private static final String JSESSIONID = "JSESSIONID";
    private static final String SET_COOKIE_PREFIX = "Set-Cookie: ";

    private final Map<String, String> cookies;

    public HttpCookies(Map<String, String> cookies) {
        createJSessionId(cookies);
        this.cookies = cookies;
    }

    private void createJSessionId(Map<String, String> cookies) {
        if (cookies.get(JSESSIONID) == null) {
            cookies.put(
                    JSESSIONID,
                    UUID.randomUUID()
                            .toString()
            );
        }
    }

    public String getCookieResponse() {
        StringBuilder stringBuilder = new StringBuilder(SET_COOKIE_PREFIX);

        for (String key : cookies.keySet()) {
            String value = cookies.get(key);
            stringBuilder.append(key)
                    .append("=")
                    .append(value)
                    .append("; ")
                    .append("Path=/;");
        }

        if (stringBuilder.lastIndexOf(";") != -1) {
            stringBuilder.deleteCharAt(stringBuilder.lastIndexOf("; "));
        }

        return stringBuilder.toString();
    }

    public String getSessionId() {
        return cookies.get(JSESSIONID);
    }
}
