package com.spring.http.enums;

import com.techcourse.exception.BadRequestException;
import java.util.Locale;

public enum HttpMethod {
    GET,
    POST;

    public static HttpMethod from(String value) {
        if (value == null) {
            throw new BadRequestException("HTTP Method는 null 일 수 없습니다.");
        }

        try {
            return HttpMethod.valueOf(value.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("지원하지 않는 HTTP Method 입니다.");
        }
    }
}
