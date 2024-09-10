package org.apache.coyote.http11.request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class QueryStrings {

    private static final String QUERY_STRING_REGEX = "^([a-zA-Z0-9%._-]+=[a-zA-Z0-9%._-]+)(&([a-zA-Z0-9%._-]+=[a-zA-Z0-9%._-]+))*$";
    private static final Pattern QUERY_STRING_PATTERN = Pattern.compile(QUERY_STRING_REGEX);
    private static final String PARAM_DELIMITER = "&";
    private static final String KEY_VALUE_DELIMITER = "=";
    private static final int PARAM_KEY_INDEX = 0;
    private static final int PARAM_VALUE_INDEX = 1;

    private final Map<String, List<String>> queryStrings;

    private QueryStrings(Map<String, List<String>> queryStrings) {
        this.queryStrings = queryStrings;
    }

    public static QueryStrings from(String source) {
        if (isQueryString(source)) {
            return new QueryStrings(parseQueryString(source));
        }
        return new QueryStrings(new HashMap<>());
    }

    private static boolean isQueryString(String source) {
        if (source == null || source.isBlank()) {
            return false;
        }
        if (QUERY_STRING_PATTERN.matcher(source).matches()) {
            return true;
        }
        return false;
    }

    private static Map<String, List<String>> parseQueryString(String source) {
        Map<String, List<String>> queryStrings = new HashMap<>();

        for (String queryString : source.split(PARAM_DELIMITER)) {
            String[] query = queryString.split(KEY_VALUE_DELIMITER);
            queryStrings.computeIfAbsent(query[PARAM_KEY_INDEX], k -> new ArrayList<>())
                    .add(query[PARAM_VALUE_INDEX]);
        }
        return queryStrings;
    }

    public String getQuery(String param) {
        if (!queryStrings.containsKey(param)) {
            throw new IllegalArgumentException("존재하지 않는 파라미터입니다.");
        }
        List<String> values = queryStrings.get(param);
        if (values.isEmpty()) {
            throw new IllegalArgumentException("존재하지 않는 파라미터입니다.");
        }
        return values.getFirst();
    }

    @Override
    public String toString() {
        return queryStrings.entrySet().stream()
                .map(entry -> entry.getKey() + ":" + entry.getValue())
                .collect(Collectors.joining(","));
    }
}
