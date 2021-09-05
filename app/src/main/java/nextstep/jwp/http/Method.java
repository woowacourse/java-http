package nextstep.jwp.http;

import nextstep.jwp.exception.NotFoundException;

import java.util.Arrays;

public enum Method {
    GET,
    POST;

    public static Method toMethod(String method){
        return Arrays.stream(values())
                .filter(it -> it.toString().equals(method))
                .findAny()
                .orElseThrow(() -> new NotFoundException("매칭되는 메서드가 없습니다."));
    }
}
