package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * key=value&... 형식의 문자열을 파싱. 쿼리 스트링과 폼 데이터가 공통으로 사용함.
 */
public class RequestParameters {

    private final Map<String, String> values;

    private RequestParameters(Map<String, String> values) {
        this.values = values;
    }

    public static RequestParameters from(String raw) {
        Map<String, String> params = new HashMap<>();
        if (raw == null || raw.isBlank()) {
            return new RequestParameters(params);
        }

        for (String param : raw.split("&")) {
            String[] keyAndValue = param.split("=", 2);
            if (keyAndValue.length == 2) {
                params.put(keyAndValue[0], keyAndValue[1]);
            }
        }

        return new RequestParameters(params);
    }

    public Optional<String> get(String name) {
        return Optional.ofNullable(values.get(name));
    }

    public boolean isEmpty() {
        return values.isEmpty();
    }
}
