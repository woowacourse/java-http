package nextstep.jwp.http;

import static java.util.stream.Collectors.toMap;

import java.util.Arrays;
import java.util.LinkedHashMap;
import nextstep.jwp.http.request.QueryParams;

public class QueryParser {

    private static final int QUERY_KEY_INDEX = 0;
    private static final int QUERY_VALUE_INDEX = 1;
    private static final int NOT_FOUND_INDEX = -1;

    private static final String QUERY_SEPARATOR = "=";
    private static final String QUERY_STARTER = "?";
    private static final String QUERY_AMPERSAND = "&";

    private final QueryParams queryParams;

    public QueryParser(String queryString) {
        int index = queryString.indexOf(QUERY_STARTER);
        if (index != NOT_FOUND_INDEX) {
            queryString = queryString.replace(QUERY_STARTER, "");
        }
        this.queryParams = parseQueryParams(queryString);
    }

    private QueryParams parseQueryParams(String queryParams) {
        return new QueryParams(
            Arrays.stream(queryParams.split(QUERY_AMPERSAND))
                .map(it -> Arrays.asList(it.split(QUERY_SEPARATOR)))
                .collect(toMap(
                    it -> it.get(QUERY_KEY_INDEX),
                    it -> it.get(QUERY_VALUE_INDEX), (o1, o2) -> o1, LinkedHashMap::new))
        );
    }

    public QueryParams queryParams() {
        return queryParams;
    }
}
