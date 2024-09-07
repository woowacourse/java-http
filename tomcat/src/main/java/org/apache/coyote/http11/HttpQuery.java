package org.apache.coyote.http11;

import com.techcourse.exception.client.BadRequestException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpQuery {

    private static final String regex = "^/\\w+\\?(?:[a-zA-Z0-9_-]+=[a-zA-Z0-9_-]+)(?:&[a-zA-Z0-9_-]+=[a-zA-Z0-9_-]+)*$";
    private static final String pairRegex = "([a-zA-Z0-9]+)=([a-zA-Z0-9]+)";
    private static final Pattern pattern = Pattern.compile(regex);
    private static final Pattern pairPattern = Pattern.compile(pairRegex);
    private static final String QUERY_START_CHAR = "?";
    private static final String QUERY_DELIMITER = "&";

    private final Map<String, String> store;

    public HttpQuery(Map<String, String> store) {
        this.store = store;
    }

    public static HttpQuery createByUri(String uri) {
        validateUri(uri);
        Map<String, String> queryMap = createQueryMap(uri);
        return new HttpQuery(queryMap);
    }

    private static void validateUri(String uri) {
        Matcher matcher = pattern.matcher(uri);
        if (!matcher.matches()) {
            throw new BadRequestException("잘못된 형식의 요청 쿼리입니다. = " + uri);
        }
    }

    private static Map<String, String> createQueryMap(String uri) {
        Map<String, String> queryMap = new HashMap<>();
        String queries = uri.substring(uri.indexOf(QUERY_START_CHAR) + 1);
        validateQueryCharacter(uri);
        for (String query : queries.split(QUERY_DELIMITER)) {
            Matcher pairMatcher = pairPattern.matcher(query);
            while (pairMatcher.find()) {
                String key = pairMatcher.group(1);
                String value = pairMatcher.group(2);
                queryMap.put(key, value);
            }
        }
        return queryMap;
    }

    private static void validateQueryCharacter(String uri) {
        if (!uri.contains(QUERY_START_CHAR)) {
            throw new BadRequestException("쿼리가 존재하지 않습니다.");
        }
    }

    public String findByKey(String key) {
        if (!store.containsKey(key)) {
            throw new BadRequestException("일치하는 쿼리가 없습니다. = " + key);
        }
        return store.get(key);
    }
}
