package org.apache.coyote.http11.request.requestLine.requestUri;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.StringTokenizer;

public class QueryParameters {

    private static final String QUERY_STRING_SEPARATOR = "&";
    private static final String QUERY_VALUE_SEPARATOR = "=";
    private static final int PARAMETER_NAME_INDEX = 0;
    private static final int PARAMETER_VALUE_INDEX = 1;

    private final Map<String, String> queryParameters;

    private QueryParameters(final Map<String, String> queryParameters) {
        this.queryParameters = queryParameters;
    }

    public static QueryParameters from(final String queryString) {
        if (queryString.isBlank()) {
            return new QueryParameters(new HashMap<>());
        }

        return parseQueryParameters(queryString);
    }

    private static QueryParameters parseQueryParameters(final String queryString) {
        final Map<String, String> queryParameters = new HashMap<>();

        final StringTokenizer stringTokenizer = new StringTokenizer(queryString, QUERY_STRING_SEPARATOR);
        while(stringTokenizer.hasMoreTokens()) {
            final String queryParameter = stringTokenizer.nextToken();
            final String[] split = queryParameter.split(QUERY_VALUE_SEPARATOR);
            queryParameters.put(split[PARAMETER_NAME_INDEX], split[PARAMETER_VALUE_INDEX]);
        }

        return new QueryParameters(queryParameters);
    }

    public Map<String, String> getQueryParameters() {
        return queryParameters;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final QueryParameters that = (QueryParameters) o;
        return Objects.equals(queryParameters, that.queryParameters);
    }

    @Override
    public int hashCode() {
        return Objects.hash(queryParameters);
    }

    @Override
    public String toString() {
        return "QueryParameters{" +
                "queryParameters=" + queryParameters +
                '}';
    }

    public String search(final String key) {
        if (!queryParameters.containsKey(key)) {
            return null;
        }

        return queryParameters.get(key);
    }
}
