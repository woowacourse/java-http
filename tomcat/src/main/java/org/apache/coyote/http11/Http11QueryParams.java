package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.exception.InvalidQueryParamKeyException;
import org.apache.coyote.KeyValueTupleParser;

public class Http11QueryParams {

    private final Map<String, String> params;

    public Http11QueryParams(final Map<String,String> params) {
        this.params = params;
    }

    public static Http11QueryParams of(final String urlQueryParams) {
        final Map<String, String> parsedTuples = KeyValueTupleParser.parse(urlQueryParams);
        return new Http11QueryParams(parsedTuples);
    }

    public static Http11QueryParams ofEmpty() {
        return new Http11QueryParams(new HashMap<>());
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
