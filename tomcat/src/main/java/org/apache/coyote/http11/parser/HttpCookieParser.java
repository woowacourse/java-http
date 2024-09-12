package org.apache.coyote.http11.parser;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.coyote.http11.data.HttpCookie;

public class HttpCookieParser {

    public static List<HttpCookie> parseCookiesFromRequest(String rawRequestCookies) {
        if (rawRequestCookies == null) {
            return List.of();
        }
        Map<String, String> parsedRawRequestCookies = Arrays.stream(rawRequestCookies.split(";"))
                .map(String::trim)
                .map(pair -> pair.split("="))
                .collect(Collectors.toMap(
                        keyValue -> keyValue[0],
                        keyValue -> keyValue[1]));
        return parsedRawRequestCookies.entrySet().stream()
                .map(entry -> new HttpCookie(entry.getKey(), entry.getValue(), Map.of()))
                .toList();
    }

    public static String formatCookieForResponse(HttpCookie httpCookie) {
        String attributes = httpCookie.getAttributes().entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("; "));
        return httpCookie.getName() + "=" + httpCookie.getValue() + "; " + attributes;
    }
}
