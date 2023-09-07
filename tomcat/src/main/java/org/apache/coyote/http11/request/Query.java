package org.apache.coyote.http11.request;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public class Query {
    private static final String QUERY_DELIMITER = "&";
    private static final String PAIR_DELIMITER = "=";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> query;

    private Query(final Map<String, String> query) {
        this.query = query;
    }

    public static Query from(final String query) {
        return new Query(Arrays.stream(query.split(QUERY_DELIMITER))
                .map(data -> data.split(PAIR_DELIMITER))
                .collect(Collectors.toMap(
                        data -> data[KEY_INDEX],
                        data -> data[VALUE_INDEX]
                )));
    }

    public static Query empty() {
        return new Query(Collections.emptyMap());
    }
}
