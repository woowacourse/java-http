package org.apache.coyote.http11.common;

import java.util.Map;
import nextstep.jwp.util.Parser;

public class HttpCookie {

    private static final String JSESSIONID = "JSESSIONID";
    private static final String DELIMITER = "=";

    private final Map<String, String> cookies;

    private HttpCookie(final Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public static HttpCookie from(final String data) {
        return new HttpCookie(Parser.parseCookieString(data));
    }

    public static String generateCookieValue(String uuid) {
        return String.join(DELIMITER, JSESSIONID, uuid);
    }
}
