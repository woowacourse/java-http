package org.apache.coyote.http11.request.line.vo;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class QueryString {

    private static final String QUERY_STRING_PATTERN = "^[\\p{L}\\d.-]+=[\\p{L}\\d.-]+(&[\\p{L}\\d.-]+=[\\p{L}\\d.-]+)*$";

    private static String QUERY_STRING_SEPARATOR = "&";
    private static String QUERY_STRING_KEY_VALUE_SEPARATOR = "=";

    private final Map<String, String> value;

    private QueryString(Map<String, String> value) {
        this.value = value;
    }

    public static QueryString from(String queryString) {
        if (queryString == null) {
            return new QueryString(null);
        }
        validateQueryString(queryString);
        return new QueryString(getQueryStringPairs(queryString));
    }

    private static void validateQueryString(String queryString) {
        Pattern compiledPattern = Pattern.compile(QUERY_STRING_PATTERN);
        Matcher matcher = compiledPattern.matcher(queryString);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("유효하지 않은 쿼리스트링입니다. 올바른 쿼리스트링인지 다시 확인해주세요.");
        }
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
