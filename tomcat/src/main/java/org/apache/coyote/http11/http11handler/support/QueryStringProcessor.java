package org.apache.coyote.http11.http11handler.support;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class QueryStringProcessor {

    private static final String QUERY_STRING_IDENTIFIER = "?";
    private static final String QUERY_STRING_IDENTIFIER_FOR_SPLIT = "\\?";
    private static final int RESOURCE_INDEX = 0;
    private static final int QUERY_STRING_INDEX = 1;
    private static final String QUERY_STRING_ELEMENT_DELIMITER = "&";
    private static final String KEY_VALUE_DELIMITER = "=";

    public boolean existQueryString(String uri) {
        return uri.contains(QUERY_STRING_IDENTIFIER);
    }

    public String removeQueryString(String uri) {
        return uri.split(QUERY_STRING_IDENTIFIER_FOR_SPLIT)[RESOURCE_INDEX];
    }

    public Map<String, String> extractQueryStringDatas(String queryString) {
        List<String> queryStringElements = List.of(queryString.split(QUERY_STRING_ELEMENT_DELIMITER));
        return queryStringElements.stream()
                .map(it -> it.split(KEY_VALUE_DELIMITER))
                .collect(Collectors.toMap(it -> it[0], it -> it[1]));
    }

    public String parseQueryString(String uri) {
        return uri.split(QUERY_STRING_IDENTIFIER_FOR_SPLIT)[QUERY_STRING_INDEX];
    }
}
