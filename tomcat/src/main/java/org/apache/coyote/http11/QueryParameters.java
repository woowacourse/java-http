package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.coyote.http11.exception.NoSuchQueryParameterException;

public class QueryParameters {

    public static final QueryParameters EMPTY_QUERY_PARAMETERS = new QueryParameters(Collections.emptyMap());
    private static final String QUERY_STRING_SPLITTER = "&";

    private final Map<String, String> values;

    public QueryParameters(final String queryString) {
        this.values = Arrays.stream(queryString.split(QUERY_STRING_SPLITTER))
                .map(QueryParameter::new)
                .collect(Collectors.toMap(QueryParameter::getKey, QueryParameter::getValue));
    }

    private QueryParameters(final Map<String, String> values) {
        this.values = values;
    }

    public String getParameterValue(final String key) {
        return values.get(key);
    }

    public String findByQueryParameterKey(final String key) {
        if (!values.containsKey(key)) {
            throw new NoSuchQueryParameterException();
        }

        return values.get(key);
    }

    public boolean isEmpty() {
        return values.isEmpty();
    }

    public Map<String, String> getValues() {
        return values;
    }
}
