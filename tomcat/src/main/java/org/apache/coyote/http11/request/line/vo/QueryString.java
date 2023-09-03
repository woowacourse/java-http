package org.apache.coyote.http11.request.line.vo;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.Collections.*;

public class QueryString {

    private static String QUERY_STRING_SEPARATOR = "&";
    private static String QUERY_STRING_KEY_VALUE_SEPARATOR = "=";

    private final Map<String, String> value;

    private QueryString(Map<String, String> value) {
        this.value = value;
    }

    public static QueryString from(String queryString) {
        if (queryString == null) {
            return new QueryString(EMPTY_MAP);
        }
        return new QueryString(getQueryStringPairs(queryString));
    }

    private static Map<String, String> getQueryStringPairs(String queryString) {
        List<String> queryStringKeyAndValue = Arrays.asList(queryString.split(QUERY_STRING_SEPARATOR));

        return queryStringKeyAndValue.stream()
                .map(keyValuePair -> keyValuePair.split(QUERY_STRING_KEY_VALUE_SEPARATOR))
                .collect(Collectors.toMap(parts -> parts[0], parts -> parts[1]));
    }

    public Map<String, String> value() {
        return value;
    }

}
