package org.apache.coyote.http11;

public class HttpToken {

    // RFC 9110 tchar 중 영숫자를 제외한 특수문자
    private static final String SPECIAL_CHARS = "!#$%&'*+-.^_`|~";

    private HttpToken() {
    }

    public static boolean isValid(final String value) {
        if (value == null || value.isEmpty()) {
            return false;
        }
        for (final char c : value.toCharArray()) {
            if (!isTokenChar(c)) {
                return false;
            }
        }
        return true;
    }

    private static boolean isTokenChar(final char c) {
        return ('A' <= c && c <= 'Z')
                || ('a' <= c && c <= 'z')
                || ('0' <= c && c <= '9')
                || SPECIAL_CHARS.indexOf(c) != -1;
    }
}
