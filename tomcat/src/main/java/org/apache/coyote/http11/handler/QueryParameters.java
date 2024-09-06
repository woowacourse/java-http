package org.apache.coyote.http11.handler;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

public class QueryParameters {
    private final Map<String, String> queryParams;

    private QueryParameters(Map<String, String> queryParams) {
        this.queryParams = Map.copyOf(queryParams);
    }

    public static QueryParameters empty() {
        return new QueryParameters(Collections.emptyMap());
    }

    public static QueryParameters parseFrom(String queryStrings) {
        Map<String, String> queryParams = new HashMap<>();
        String[] queryParamTokens = queryStrings.split("&");
        for (String queryParam : queryParamTokens) {
            String[] split = queryParam.split("=");
            queryParams.put(split[0], split[1]);
        }
        return new QueryParameters(queryParams);
    }

    public String getParam(String key) {
        return Optional.ofNullable(queryParams.get(key))
                .orElseThrow(() -> new NoSuchElementException(key + " 에 해당하는 값이 존재하지 않습니다."));
    }
}
