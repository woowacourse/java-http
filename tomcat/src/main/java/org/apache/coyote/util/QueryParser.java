package org.apache.coyote.util;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toUnmodifiableList;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.apache.coyote.common.QueryString;

public class QueryParser {

    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private QueryParser() {
    }

    public static QueryString parse(String query) {
        if (query == null || query.isBlank()) {
            return QueryString.EMPTY;
        }
        Map<String, List<String>> queryString = Arrays.stream(query.split("&"))
            .map(queries -> queries.split("=", 2))
            .collect(groupingBy(queries -> queries[KEY_INDEX], mapping(QueryParser::parseQueries, toUnmodifiableList())));
        return new QueryString(queryString);
    }

    private static String parseQueries(String[] queries) {
        if (queries.length > 1) {
            return queries[VALUE_INDEX];
        }
        return "";
    }
}
