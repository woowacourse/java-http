package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class QueryParameters {

    private final Map<String, QueryParameter> data = new HashMap<>();

    public void put(String original) {
        QueryParameter queryParam = QueryParameter.from(original);
        if (data.containsKey(queryParam.key())) {
            throw new IllegalArgumentException("중복된 쿼리 파라미터가 전달되었습니다.");
        }
        data.put(queryParam.key(), queryParam);
    }

    public String get(String key) {
        return data.get(key).value();
    }
}
