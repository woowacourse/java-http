package org.apache.coyote.http11;

import java.util.Map;
import java.util.UUID;

public class HttpCookies {

    private static final String JSESSIONID = "JSESSIONID";
    private static final String SET_COOKIE_PREFIX = "Set-Cookie: ";

    private final Map<String, String> cookies;
    private boolean isSetCookieNeed = false;

    public HttpCookies(Map<String, String> cookies) {
        createJSessionId(cookies);
        this.cookies = cookies;
    }

    private void createJSessionId(Map<String, String> cookies) {
        if (cookies.get(JSESSIONID) == null) {
            this.isSetCookieNeed = true;
            cookies.put(
                    JSESSIONID,
                    UUID.randomUUID()
                            .toString()
            );
        }
    }

    public String getCookieResponse() {
        StringBuilder stringBuilder = new StringBuilder(SET_COOKIE_PREFIX);

        if (!isSetCookieNeed) {
            return "";
        }

        for (String key : cookies.keySet()) {
            String value = cookies.get(key);
            stringBuilder.append(key)
                    .append("=")
                    .append(value)
                    .append("; ");
        }

        if (stringBuilder.lastIndexOf(";") != -1) {
            stringBuilder.deleteCharAt(stringBuilder.lastIndexOf(";"));
        }
        return stringBuilder.toString();
    }
}
