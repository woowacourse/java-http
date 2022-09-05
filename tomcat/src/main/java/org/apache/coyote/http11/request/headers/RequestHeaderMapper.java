package org.apache.coyote.http11.request.headers;

import java.util.Arrays;
import java.util.function.BiFunction;
import org.apache.coyote.http11.header.HttpCookie;
import org.apache.coyote.http11.response.headers.ContentLength;

public enum RequestHeaderMapper {

    COOKIE("Cookie", HttpCookie::parse),
    CONTENT_LENGTH("Content-Length", (String field, String value) -> new ContentLength(value)),
    ANONYMOUS("", AnonymousRequestHeader::new),
    ;

    private final String signature;
    private final BiFunction<String, String, RequestHeader> mapFunction;

    RequestHeaderMapper(String signature, BiFunction<String, String, RequestHeader> mapFunction) {
        this.signature = signature;
        this.mapFunction = mapFunction;
    }

    public static RequestHeader findAndApply(String field, String value) {
        RequestHeaderMapper requestHeaderMapper = Arrays.stream(values())
                .filter(it -> it.signature.equals(field))
                .findAny()
                .orElse(ANONYMOUS);
        return requestHeaderMapper.apply(field, value);
    }

    public RequestHeader apply(String field, String value) {
        return mapFunction.apply(field, value);
    }
}
