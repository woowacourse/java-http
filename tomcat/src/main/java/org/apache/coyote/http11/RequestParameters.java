package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class RequestParameters {

    public static final RequestParameters EMPTY_PARAMETERS = new RequestParameters(Collections.emptyMap());
    private static final String QUERY_STRING_SPLITTER = "&";

    private final Map<String, String> values;

    public RequestParameters(final String queryString) {
        this.values = Arrays.stream(queryString.split(QUERY_STRING_SPLITTER))
                .map(RequestParameter::new)
                .collect(Collectors.toMap(RequestParameter::getKey, RequestParameter::getValue));
    }

    private RequestParameters(final Map<String, String> values) {
        this.values = values;
    }

    public boolean hasParameter(final String... keys) {
        Set<String> keySet = values.keySet();
        return keySet.containsAll(List.of(keys));
    }

    public String getParameter(final String key) {
        return values.get(key);
    }
}
