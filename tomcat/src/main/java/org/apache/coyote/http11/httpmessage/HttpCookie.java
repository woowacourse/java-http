package org.apache.coyote.http11.httpmessage;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpCookie {

    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;
    private static final String SPACE = " ";
    private static final String COOKIE_DELIMITER = "=";

    private final Map<String, String> cookies;

    public HttpCookie(final Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public static HttpCookie from(final String cookieValues) {
        final Map<String, String> cookies = Arrays.stream(cookieValues.split(SPACE))
            .map(headerContent -> headerContent.split(COOKIE_DELIMITER))
            .collect(Collectors.toMap(
                headerContent -> headerContent[KEY_INDEX],
                headerContent -> headerContent[VALUE_INDEX]));
        return new HttpCookie(cookies);
    }

    public String getAuthorizedCookie() {
        if (cookies.containsKey("JSESSIONID")) {
            return cookies.get("JSESSIONID");
        }
        return "";
    }

    public Map<String, String> getCookies() {
        return cookies;
    }
}
