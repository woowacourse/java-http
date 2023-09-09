package org.apache.coyote.http11.httpmessage;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpCookie {

    private final Map<String, String> cookies;

    public HttpCookie(final Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public static HttpCookie from(final String cookieValues) {
        final Map<String, String> cookies = Arrays.stream(cookieValues.split(" "))
            .map(headerContent -> headerContent.split("="))
            .collect(Collectors.toMap(
                headerContent -> headerContent[0],
                headerContent -> headerContent[1]));
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
