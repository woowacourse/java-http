package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;

public class Parameters {

    private final Map<String, QueryParameter> data = new HashMap<>();

    public void put(String original) {
        QueryParameter queryParam = QueryParameter.from(original);
        if (data.containsKey(queryParam.key())) {
            throw new IllegalArgumentException("중복된 쿼리 파라미터가 전달되었습니다.");
        }
        data.put(queryParam.key(), queryParam);
    }

    public String get(String key) {
        QueryParameter queryParameter = data.get(key);
        if (queryParameter == null) {
            return null;
        }
        return queryParameter.value();
    }

    public boolean isEmpty() {
        return data.isEmpty();
    }
}
