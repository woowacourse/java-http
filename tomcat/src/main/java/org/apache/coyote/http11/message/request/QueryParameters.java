package org.apache.coyote.http11.message.request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class QueryParameters {
    private final Map<String, List<String>> parameters;

    public QueryParameters(Map<String, List<String>> parameters) {
        this.parameters = new HashMap<>(parameters);
    }

    public String getSingleValueByKey(String key) {
        if (!parameters.containsKey(key)) {
            throw new IllegalArgumentException(String.format("key %s에 해당되는 쿼리 파라미터가 존재하지 않습니다.", key));
        }

        return parameters.get(key).getFirst();
    }
}
