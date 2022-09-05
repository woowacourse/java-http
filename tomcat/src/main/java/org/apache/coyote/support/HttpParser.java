package org.apache.coyote.support;

import static java.util.Arrays.stream;

import java.util.Map;
import java.util.stream.Collectors;

public class HttpParser {

    /**
     * 다음과 같은 query string에 대해  parsing을 합니다
     * <p>
     * 1. "/login?account=philz&password=1234"
     * <p>
     * 2. "account=philz&password=1234"
     * <p>
     * 3. ["account=philz", "password=1234"]
     * <p>
     * 4. {"account" : "philz", "password" : "1234"}
     */
    public static Map<String, String> parseUri(final String uri) {
        return parseQueryString(uri.split("\\?")[1]);
    }

    /**
     * 다음과 같은 query string에 대해  parsing을 합니다
     * <p>
     * 1. "account=philz&password=1234"
     * <p>
     * 2. ["account=philz", "password=1234"]
     * <p>
     * 3. {"account" : "philz", "password" : "1234"}
     */
    public static  Map<String, String> parseQueryString(final String uri) {
        final String[] queryStringArr = uri.split("\\&");

        return stream(queryStringArr)
                .map(it -> it.split("\\="))
                .collect(Collectors.toMap(it -> it[0], it -> it[1]));
    }
}
