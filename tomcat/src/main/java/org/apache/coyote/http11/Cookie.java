package org.apache.coyote.http11;

public class Cookie {

    public static String getValue(final String cookieHeader,
                                  final String cookieName) {
        if (cookieHeader == null || cookieHeader.isBlank()) {
            return null;
        }

        for (String cookie : cookieHeader.split(";")) {
            final String[] keyValue = cookie.trim().split("=", 2);

            if (keyValue.length == 2 && cookieName.equals(keyValue[0].trim())) {
                return keyValue[1].trim();
            }
        }

        return null;
    }

    public static boolean contains(final String cookieHeader,
                                   final String cookieName) {
        return getValue(cookieHeader, cookieName) != null;
    }

    private Cookie() {
    }
}
