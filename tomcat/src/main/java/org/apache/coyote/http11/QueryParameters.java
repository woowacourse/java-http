package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class QueryParameters {

    private static final String QUERY_STRING_SPLITTER = "&";

    private final Map<String, String> values;

    public QueryParameters(final String queryString) {
        this.values = Arrays.stream(queryString.split(QUERY_STRING_SPLITTER))
                .map(QueryParameter::new)
                .collect(Collectors.toMap(QueryParameter::getKey, QueryParameter::getValue));
    }

    public String getParameterValue(final String key) {
        return values.get(key);
    }

    public Map<String, String> getValues() {
        return values;
    }
}
