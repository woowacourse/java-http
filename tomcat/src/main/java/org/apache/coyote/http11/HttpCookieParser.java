package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpCookieParser {

    public static HttpCookie parseCookiesFromRequest(String rawRequestCookies) {
        if (rawRequestCookies == null) {
            return new HttpCookie(Map.of());
        }
        Map<String, String> parsedRawRequestCookies =  Arrays.stream(rawRequestCookies.split(";"))
                .map(String::trim)
                .map(pair -> pair.split("="))
                .collect(Collectors.toMap(
                        keyValue -> keyValue[0],
                        keyValue -> keyValue[1]));
        return new HttpCookie(parsedRawRequestCookies);
    }

    public static String formatCookiesForResponse(HttpCookie httpCookie) {
        return httpCookie.getCookies().entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("; "));
    }
}
