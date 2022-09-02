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
        List<String> parameters = List.of(this.query.split("&"));

        for (String parameter : parameters) {
            List<String> pair = List.of(parameter.split("="));
            if (pair.size() == 1) {
                result.put(pair.get(0), "");
                continue;
            }
            result.put(pair.get(0), pair.get(1));
        }

        return result;
    }
}
