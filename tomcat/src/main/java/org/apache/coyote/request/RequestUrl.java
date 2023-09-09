package org.apache.coyote.request;

import java.util.HashMap;
import java.util.Map;

public class RequestUrl {

    private static final String QUERY_STRING_SPLIT_DELIMITER = "\\?";
    private static final String QUERY_STRING_KEY_PAIR_SPLIT_DELIMITER = "&";
    private static final String QUERY_STRING_KEY_VALUE_SPLIT_DELIMITER = "=";

    private final String path;
    private final Map<String, String> queryString;

    private RequestUrl(String path, Map<String, String> queryString) {
        this.path = path;
        this.queryString = queryString;
    }

    public static RequestUrl from(String url) {
        if (hasNoQueryString(url)) {
            return new RequestUrl(url, new HashMap<>());
        }
        String path = url.split(QUERY_STRING_SPLIT_DELIMITER)[0];

        String queries = url.split(QUERY_STRING_SPLIT_DELIMITER)[1];
        Map<String, String> queryString = getQueryString(queries);

        return new RequestUrl(path, queryString);
    }

    private static boolean hasNoQueryString(String url) {
        return url.split(QUERY_STRING_SPLIT_DELIMITER).length == 1;
    }

    private static Map<String, String> getQueryString(String queries) {
        Map<String, String> queryString = new HashMap<>();
        for (String query : queries.split(QUERY_STRING_KEY_PAIR_SPLIT_DELIMITER)) {
            String key = query.split(QUERY_STRING_KEY_VALUE_SPLIT_DELIMITER)[0];
            String value = query.split(QUERY_STRING_KEY_VALUE_SPLIT_DELIMITER)[1];
            queryString.put(key, value);
        }
        return queryString;
    }

    public boolean isSamePath(String otherPath) {
        return path.equals(otherPath);
    }

    public String getQueryValue(String key) {
        return queryString.getOrDefault(key, null);
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getQueryString() {
        return queryString;
    }
}
