package org.apache.coyote.http11;

import com.techcourse.exception.client.BadRequestException;
import java.util.Arrays;

public enum HttpMethod {

    GET, POST;

    public static HttpMethod findByName(String name) {
        return Arrays.stream(values())
                .filter(httpMethod -> httpMethod.name().equals(name))
                .findAny()
                .orElseThrow(() -> new BadRequestException("잘못된 HTTP 메서드입니다. = " + name));
    }

    public boolean isGet() {
        return this == GET;
    }

    public boolean isPost() {
        return this == POST;
    }
}
