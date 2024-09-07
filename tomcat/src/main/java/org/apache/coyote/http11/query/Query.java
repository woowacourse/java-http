package org.apache.coyote.http11.query;

import com.techcourse.exception.client.BadRequestException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Query {

    private static final String pairRegex = "([a-zA-Z0-9]+)=([a-zA-Z0-9]+)";
    private static final Pattern pairPattern = Pattern.compile(pairRegex);

    private static final String QUERY_DELIMITER = "&";

    private final Map<String, String> store;

    public Query(Map<String, String> store) {
        this.store = store;
    }

    public static Query create(String queries) {
        Map<String, String> queryMap = new HashMap<>();
        for (String query : queries.split(QUERY_DELIMITER)) {
            Matcher pairMatcher = pairPattern.matcher(query);
            while (pairMatcher.find()) {
                String key = pairMatcher.group(1);
                String value = pairMatcher.group(2);
                queryMap.put(key, value);
            }
        }
        return new Query(queryMap);
    }

    public String findByKey(String key) {
        if (!store.containsKey(key)) {
            throw new BadRequestException("일치하는 쿼리가 없습니다. = " + key);
        }
        return store.get(key);
    }
}
