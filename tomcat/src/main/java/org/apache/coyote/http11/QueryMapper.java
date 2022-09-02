package org.apache.coyote.http11;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueryMapper {

    private final String query;

    public QueryMapper(URI uri) {
        this.query = uri.getQuery();
    }

    public Map<String, String> getParameters() {
        Map<String, String> result = new HashMap<>();
        List<String> parameterPairs = List.of(this.query.split("&"));

        for (String parameterPair : parameterPairs) {
            insertParameter(result, parameterPair);
        }

        return result;
    }

    private static void insertParameter(Map<String, String> result, String parameterPair) {
        List<String> pair = List.of(parameterPair.split("="));

        if (pair.size() == 1) {
            result.put(pair.get(0), "");
            return;
        }
        result.put(pair.get(0), pair.get(1));
    }
}
