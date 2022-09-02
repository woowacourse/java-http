package org.apache.coyote.http11.request.spec;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public class QueryParams {

    private static final int VALID_KEY_VALUE_COUNT = 2;
    private static final String PARAM_DELIMITER = "&";
    private static final String KEY_VALUE_DELIMITER = "=";

    private final Map<String, String> value;

    public QueryParams(Map<String, String> value) {
        this.value = value;
    }

    public static QueryParams from(String queryString) {
        return new QueryParams(parse(queryString));
    }

    /**
     * @Return QueryParams instance with empty value
     */
    public static QueryParams empty() {
        return new QueryParams(Collections.emptyMap());
    }

    private static Map<String, String> parse(String queryString) {
        String[] keyValues = queryString.split(PARAM_DELIMITER);
        return Arrays.stream(keyValues)
                .map(kv -> {
                    String[] components = kv.split(KEY_VALUE_DELIMITER);
                    if (components.length != VALID_KEY_VALUE_COUNT) {
                        throw new IllegalArgumentException("key-value 쌍이 맞지 않습니다. = " + Arrays.toString(components));
                    }
                    return components;
                })
                .collect(Collectors.toMap(components -> components[0], components -> components[1]));
    }

    public boolean hasParams() {
        return !value.isEmpty();
    }

    public String get(String key) {
        if (!value.containsKey(key)) {
            throw new IllegalArgumentException("존재하지 않는 key 입니다. key = " + key);
        }
        return value.get(key);
    }

    public int size() {
        return value.size();
    }

    public Map<String, String> getParams() {
        return value;
    }
}
