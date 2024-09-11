package org.apache.coyote.http11.request;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public class QueryParameter {

    private static final String PARAMETER_DELIMITER = "&";
    private static final String KEY_VALUE_DELIMITER = "=";

    private final Map<String, String> parameters;

    public QueryParameter(String queryParameter) {
        validateQueryParameter(queryParameter);
        this.parameters = toMap(queryParameter);
    }

    private void validateQueryParameter(String queryParameter) {
        if (queryParameter == null || queryParameter.isEmpty()) {
            throw new IllegalArgumentException("Query Parameter 값이 존재하지 않습니다.");
        }
    }

    private Map<String, String> toMap(String queryParameter) {
        Map<String, String> parameters = new LinkedHashMap<>();

        for (String parameter : queryParameter.split(PARAMETER_DELIMITER)) {
            String[] keyAndValue = parameter.split(KEY_VALUE_DELIMITER);
            parameters.put(decode(keyAndValue[0]), decode(keyAndValue[1]));
        }

        return parameters;
    }

    private String decode(String source) {
        return URLDecoder.decode(source, StandardCharsets.UTF_8);
    }

    public String get(String key) {
        if (parameters.containsKey(key)) {
            return parameters.get(key);
        }
        throw new IllegalArgumentException("입력된 값에 해당하는 결과를 찾을 수 없습니다. 입력된 값: " + key);
    }
}
