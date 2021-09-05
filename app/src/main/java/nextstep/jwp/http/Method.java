package nextstep.jwp.http;

import java.util.Arrays;

public enum Method {
    GET,
    POST;

    public static Method toMethod(String method){
        return Arrays.stream(values())
                .filter(it -> it.toString().equals(method))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Does not have proper method"));
    }
}
