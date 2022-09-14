package nextstep.jwp.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class FormData {
    private static final String QUERY_STRING_PAIR_DELIMITER = "=";
    private static final int SPLIT_SIZE = 2;
    private static final int QUERY_STRING_KEY_INDEX = 0;
    private static final int QUERY_STRING_VALUE_INDEX = 1;
    private static final String DEFAULT_VALUE = "";

    private final Map<String, String> formData;

    private FormData(Map<String, String> formData) {
        this.formData = formData;
    }

    public static FormData from(String[] data) {
        Map<String, String> queryStringMap = new HashMap<>();
        boolean isNotValidPair = Arrays.stream(data)
                .anyMatch(eachQuery ->
                        eachQuery.split(QUERY_STRING_PAIR_DELIMITER).length != SPLIT_SIZE);
        if (isNotValidPair) {
            return new FormData(Collections.emptyMap());
        }
        for (String eachQuery : data) {
            String[] parsedEntry = eachQuery.split(QUERY_STRING_PAIR_DELIMITER);
            queryStringMap.put(parsedEntry[QUERY_STRING_KEY_INDEX], parsedEntry[QUERY_STRING_VALUE_INDEX]);
        }
        return new FormData(queryStringMap);
    }

    public boolean isEmpty() {
        return this.formData.isEmpty();
    }

    public String get(String key) {
        return this.formData.getOrDefault(key, DEFAULT_VALUE);
    }
}
