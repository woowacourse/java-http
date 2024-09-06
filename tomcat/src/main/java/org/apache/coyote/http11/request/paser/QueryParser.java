package org.apache.coyote.http11.request.paser;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class QueryParser {

    private final Map<String, String> parsedQueries;

    public QueryParser(String query) {
        this.parsedQueries = parse(query);
    }

    private Map<String, String> parse(String query) {
        List<String> keys = List.of(query.split("&"));
        return keys.stream()
                .map(key -> key.split("="))
                .collect(Collectors.toMap(
                        keyValue -> keyValue[0],
                        keyValue -> keyValue[1]
                ));
    }

    public String findValue(String key) {
        if (parsedQueries.containsKey(key)) {
            return parsedQueries.get(key);
        }
        return null;
    }
}
