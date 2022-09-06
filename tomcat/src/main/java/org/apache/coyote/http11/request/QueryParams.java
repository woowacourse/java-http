package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.exception.InvalidQueryParamKeyException;
import org.apache.coyote.KeyValueTupleParser;

public class QueryParams {

    private final Map<String, String> params;

    public QueryParams(final Map<String,String> params) {
        this.params = params;
    }

    public static QueryParams of(final String urlQueryParams) {
        final Map<String, String> parsedTuples = KeyValueTupleParser.parse(urlQueryParams);
        return new QueryParams(parsedTuples);
    }

    public static QueryParams ofEmpty() {
        return new QueryParams(new HashMap<>());
    }

    public String get(final String key) {
        final String value = params.get(key);
        if (value == null) {
            throw new InvalidQueryParamKeyException();
        }
        return value;
    }

    public boolean isEmpty() {
        return this.params.isEmpty();
    }
}
