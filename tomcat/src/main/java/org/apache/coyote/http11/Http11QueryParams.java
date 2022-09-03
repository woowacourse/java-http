package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class Http11QueryParams {

    private final Map<String, String> queryParams;

    private Http11QueryParams(Map<String, String> queryParams) {
        this.queryParams = queryParams;
    }

    public static Http11QueryParams from(String url) {
        final int index = url.indexOf("?");
        final String[] keyValues = url.substring(index + 1).split("&");
        final Map<String, String> queryParams = Arrays.stream(keyValues)
                .map(keyValue -> keyValue.split("="))
                .collect(Collectors.toMap(keyValue -> keyValue[0], keyValue -> keyValue[1]));
        return new Http11QueryParams(queryParams);
    }

    public String getValueFrom(String key) {
        final String value = queryParams.get(key);
        if (value == null) {
            throw new IllegalArgumentException("존재하지 않는 파라미터입니다.");
        }
        return value;
    }
}
