package org.apache.coyote.http11.request.element;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class Query {

    private static final String PATH_DELIMITER = "?";
    private static final String QUERY_DELIMITER = "&";
    private static final String PARAM_DELIMITER = "=";

    private final Map<String, String> params;

    public Query(String uri) {
        this.params = extractQuery(uri);
    }

    private Map<String, String> extractQuery(String uri) {
        String[] query = uri.substring(uri.indexOf(PATH_DELIMITER) + 1)
                .split(QUERY_DELIMITER);
        return Arrays.stream(query)
                .map(element -> element.split(PARAM_DELIMITER))
                .collect(Collectors.toMap(split -> split[0], split -> split[1]));
    }

    public String find(String param) {
        return params.get(param);
    }
}
