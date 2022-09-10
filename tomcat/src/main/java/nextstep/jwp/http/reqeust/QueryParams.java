package nextstep.jwp.http.reqeust;

import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.exception.QueryStringFormatException;

public class QueryParams {

    private static final String QUERY_CONNECTOR = "&";
    private static final String QUERY_SEPARATOR = "=";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> values;

    public QueryParams(final String queryString) {
        this.values = new HashMap<>();

        if (queryString == null) {
            throw new QueryStringFormatException();
        }
        String[] queries = queryString.split(QUERY_CONNECTOR);
        addQueries(queries);
    }

    private void addQueries(final String[] queries) {
        for (String query : queries) {
            validateQueryFormat(query);
            String[] value = query.split(QUERY_SEPARATOR);
            this.values.put(value[KEY_INDEX], value[VALUE_INDEX]);
        }
    }

    private void validateQueryFormat(final String query) {
        if (!query.contains(QUERY_SEPARATOR) || query.isBlank()) {
            throw new QueryStringFormatException();
        }
        if (query.split(QUERY_SEPARATOR).length != 2) {
            throw new QueryStringFormatException();
        }
    }

    public Map<String, String> getValues() {
        return values;
    }
}
