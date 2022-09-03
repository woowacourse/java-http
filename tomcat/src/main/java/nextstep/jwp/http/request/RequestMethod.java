package nextstep.jwp.http.request;

import java.util.Arrays;
import nextstep.jwp.exception.InvalidRequestMethodException;

public enum RequestMethod {

    GET("GET"),
    POST("POST")
    ;

    private final String value;

    RequestMethod(final String value) {
        this.value = value;
    }

    public static RequestMethod find(final String method) {
        return Arrays.stream(RequestMethod.values())
            .filter(value -> value.name().equals(method))
            .findAny()
            .orElseThrow(InvalidRequestMethodException::new);
    }

    public String getValue() {
        return value;
    }
}
