package org.apache.coyote.http11;

import java.util.LinkedHashMap;
import java.util.Map;

public class Uri {
    private final String uri;
    private final String path;
    private final Map<String, String> queryMap;

    private Uri(String uri, String path, Map<String, String> queryMap) {
        this.uri = uri;
        this.path = path;
        this.queryMap = queryMap;
    }

    public static Uri create(String uri) {
        String[] devidedUrlQuery = uri.split("\\?");
        boolean isQuery = devidedUrlQuery.length > 1;
        String path = devidedUrlQuery[0];
        Map<String, String> queryMap = new LinkedHashMap<>();
        if (isQuery) {
            seperateQuery(devidedUrlQuery, queryMap);
        }
        return new Uri(uri, path, queryMap);
    }

    private static void seperateQuery(String[] devidedUrlQuery, Map<String, String> queryMap) {
        String queryString = devidedUrlQuery[1];
        if (queryString != null && !queryString.isBlank()) {
            String[] query = queryString.split("&");
            for (int i = 0; i < query.length; i++) {
                String[] keyValue = query[i].split("=", 2);
                String value = keyValue.length > 1 ? keyValue[1] : "";
                queryMap.put(keyValue[0], value);
            }
        }
    }

    public String getPath() {
        return path;
    }
}
