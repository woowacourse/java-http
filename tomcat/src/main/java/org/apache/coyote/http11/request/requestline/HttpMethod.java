package org.apache.coyote.http11.request.requestline;


import org.apache.coyote.http11.HttpToken;
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

    static HttpMethod from(final String token) {
        if (token == null) {
            throw new BadRequestException("메서드가 없습니다");
        }
        final HttpMethod method = BY_NAME.get(token);
        if (method != null) {
            return method;
        }
        if (!HttpToken.isValid(token)) {
            throw new BadRequestException("잘못된 메서드 형식입니다");
        }
        throw new NotImplementedException("지원하지 않는 메서드입니다");
    }
}
