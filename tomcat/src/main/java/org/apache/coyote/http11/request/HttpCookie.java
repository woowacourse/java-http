package org.apache.coyote.http11.request;

public class HttpCookie {

    public static String extractCookieValue(String cookieHeader, String targetName) {
        String[] parts = cookieHeader.split(";");
        for (String part : parts) {
            String trimmed = part.trim();
            int idx = trimmed.indexOf('=');
            if (idx <= 0) {
                continue;
            }
            String name = trimmed.substring(0, idx).trim();
            String value = trimmed.substring(idx + 1).trim();
            if (name.equals(targetName)) {
                return value;
            }
        }
        return null;
    }
}
