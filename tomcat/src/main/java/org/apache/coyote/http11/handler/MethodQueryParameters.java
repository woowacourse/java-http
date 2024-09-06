package org.apache.coyote.http11.handler;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

public class MethodQueryParameters {
    private final Map<String, String> queryParams;

    private MethodQueryParameters(Map<String, String> queryParams) {
        this.queryParams = Map.copyOf(queryParams);
    }

    public static MethodQueryParameters empty() {
        return new MethodQueryParameters(Collections.emptyMap());
    }

    public static MethodQueryParameters parseFrom(String queryStrings) {
        Map<String, String> queryParams = new HashMap<>();
        String[] queryParamTokens = queryStrings.split("&");
        for (String queryParam : queryParamTokens) {
            String[] split = queryParam.split("=");
            queryParams.put(split[0], split[1]);
        }
        return new MethodQueryParameters(queryParams);
    }

    public String getParam(String key) {
        return Optional.ofNullable(queryParams.get(key))
                .orElseThrow(() -> new NoSuchElementException(key + " 에 해당하는 값이 존재하지 않습니다."));
    }
}
