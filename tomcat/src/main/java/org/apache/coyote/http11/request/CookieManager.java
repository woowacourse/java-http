package org.apache.coyote.http11.request;

public class CookieManager {

    public static String getCookieValue(String rawCookie, String targetCookieName) {
        if (rawCookie != null) {
            String[] cookieEntries = rawCookie.split(";");
            for (String cookieEntry : cookieEntries) {
                String[] split = cookieEntry.split("=");
                if (split.length == 2 && !split[0].isBlank() && !split[1].isBlank()) {
                    if (split[0].equals(targetCookieName)) {
                        return split[1];
                    }
                }
            }
        }
        return null;
    }
}
