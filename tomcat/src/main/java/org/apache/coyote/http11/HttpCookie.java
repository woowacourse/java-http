package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpCookie {
    private static final String JSESSIONID_KEY = "JSESSIONID";

    private Map<String, String> cookie;

    public HttpCookie(String cookieLines) {
        this.cookie = convertToCookie(cookieLines);;
    }

    private Map<String, String> convertToCookie(String cookieLines) {
        return Arrays.stream(cookieLines.split("; "))
                .map(cookieLine -> cookieLine.split("="))
                .collect(Collectors.toMap(
                        entrySet -> entrySet[0],
                        entrySet -> entrySet[1]
                ));
    }

    public boolean isSessionIdNotExist() {
        return !cookie.containsKey(JSESSIONID_KEY);
    }
}
