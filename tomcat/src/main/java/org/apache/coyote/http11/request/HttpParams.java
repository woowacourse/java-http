package org.apache.coyote.http11.request;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.coyote.http11.BadRequestException;

public class HttpParams {
    private final Map<String, String> queryParams;

    public HttpParams(Map<String, String> queryParams) {
        this.queryParams = queryParams;
    }

    public static HttpParams from(String queryInfo) {
        if(queryInfo == null) {
            return new HttpParams(Map.of());
        }

        Map<String, String> params = Arrays.stream(queryInfo.split("&"))
                .map(parameter -> parameter.split("=", 2))
                .filter(parameter -> parameter.length == 2)
                .collect(Collectors.toUnmodifiableMap(
                        parameter -> decode(parameter[0]),
                        parameter -> decode(parameter[1]),
                        (previous, replacement) -> replacement
                ));
        return new HttpParams(params);
    }

    public String get(String key) {
        return queryParams.get(key);
    }

    private static String decode(String value) {
        try {
            return URLDecoder.decode(value, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("잘못된 쿼리 스트링입니다.", e);
        }
    }
}
