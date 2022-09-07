package org.apache.coyote.http11;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueryMapper {

    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final String query;

    public QueryMapper(URI uri) {
        this.query = uri.getQuery();
    }

    public QueryMapper(String body) {
        this.query = body;
    }

    public Map<String, String> getParameters() {
        Map<String, String> result = new HashMap<>();
        List<String> parameterPairs = List.of(this.query.split("&"));

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
}
