package org.apache.coyote.http11.request.requestline;


import org.apache.coyote.http11.exception.BadRequestException;
import org.apache.coyote.http11.exception.NotImplementedException;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum HttpMethod {
    GET, POST, PUT, DELETE, HEAD;

    private static final Map<String, HttpMethod> BY_NAME = Arrays.stream(values())
            .collect(Collectors.toUnmodifiableMap(Enum::name, Function.identity()));

    // RFC 9110 tchar 중 영숫자를 제외한 특수문자
    private static final String TOKEN_SPECIAL_CHARS = "!#$%&'*+-.^_`|~";

    static HttpMethod from(final String token) {
        if (token == null) {
            throw new BadRequestException("메서드가 없습니다");
        }
        final HttpMethod method = BY_NAME.get(token);
        if (method != null) {
            return method;
        }
        if (!isValidToken(token)) {
            throw new BadRequestException("잘못된 메서드 형식입니다: " + token);
        }
        throw new NotImplementedException("지원하지 않는 메서드입니다: " + token);
    }

    private static boolean isValidToken(final String token) {
        if (token.isEmpty()) {
            return false;
        }
        for (final char c : token.toCharArray()) {
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
                || TOKEN_SPECIAL_CHARS.indexOf(c) != -1;
    }
}
