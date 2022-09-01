package nextstep.jwp.http.request;

import java.util.Arrays;
import nextstep.jwp.exception.InvalidHttpVersionException;

public enum HttpVersion {

    HTTP_1_1
    ;

    public static HttpVersion from(final String version) {
        return Arrays.stream(HttpVersion.values())
            .filter(value -> value.name().equals(version))
            .findAny()
            .orElseThrow(InvalidHttpVersionException::new);
    }
}
