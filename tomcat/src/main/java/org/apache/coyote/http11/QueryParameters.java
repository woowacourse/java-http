package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.coyote.http11.request.RequestBody;

public class QueryParameters {

    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> value;

    public QueryParameters(RequestBody body) {
        this.value = getMappedQuery(body.getValue());
    }

    private Map<String, String> getMappedQuery(String query) {
        Map<String, String> result = new HashMap<>();
        List<String> parameterPairs = List.of(query.split("&"));

        for (String parameterPair : parameterPairs) {
            insertParameter(result, parameterPair);
        }

        return result;
    }

    private void insertParameter(Map<String, String> result, String parameterPair) {
        List<String> pair = List.of(parameterPair.split("="));

        if (pair.size() == 1) {
            result.put(pair.get(KEY_INDEX), "");
            return;
        }
        result.put(pair.get(KEY_INDEX), pair.get(VALUE_INDEX));
    }

    public Map<String, String> getValue() {
        return value;
    }
}
