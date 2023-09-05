package org.apache.coyote.http11.request;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toMap;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Stream;

public class QueryStrings {

    public static final QueryStrings EMPTY = new QueryStrings(Collections.emptyMap());

    private final Map<String, String> queryParameter;

    private QueryStrings(Map<String, String> queryParameter) {
        this.queryParameter = queryParameter;
    }

    public static QueryStrings from(String queryString) {
        return Stream.of(queryString.split("&"))
                .map(query -> query.split("="))
                .collect(collectingAndThen(
                        toMap(parts -> parts[0], strings -> strings[1]),
                        QueryStrings::new
                ));
    }

    public String findValue(String key) {
        return queryParameter.get(key);
    }

    public boolean isExisted() {
        return !queryParameter.isEmpty();
    }
}
