package org.apache.coyote.http.request.line;

import com.techcourse.exception.UncheckedServletException;
import java.util.Arrays;

public enum Method {

    GET,
    POST;

    public static Method from(String method) {
        return Arrays.stream(Method.values())
                .filter(m -> method.equals(m.name()))
                .findAny()
                .orElseThrow(() -> new UncheckedServletException("지원하지 않는 HTTP 메서드입니다."));
    }
}
