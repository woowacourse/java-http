package org.apache.coyote.http11.util;

public final class CookieUtils {

    private CookieUtils() {}

    public static String buildSetCookie(
            String name,
            String value,
            boolean httpOnly,
            String path
    ) {
        StringBuilder sb = new StringBuilder().append(name).append("=").append(value);
        sb.append("; Path=").append(path != null ? path : "/");
        if (httpOnly) {
            sb.append("; HttpOnly");
        }
        sb.append("; SameSite=Lax");
        return sb.toString();
    }
}
