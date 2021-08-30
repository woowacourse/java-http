package nextstep.jwp.model;

import java.util.HashMap;
import java.util.Map;

public class Uri {

    private final String path;
    private String uri;
    private Map<String, String> queryMap;

    private Uri(String path, String uri, Map<String, String> queryMap) {
        this.path = path;
        this.uri = uri;
        this.queryMap = queryMap;
    }

    public static Uri of(String path) {
        if (!path.contains("?")) {
            return new Uri(path, path, null);
        }
        int index = path.indexOf("?");
        String uri = path.substring(0, index);
        String queries = path.substring(index + 1);
        Map<String, String> queryMap = collectQuery(queries);
        return new Uri(path, uri, queryMap);
    }

    private static Map<String, String> collectQuery(String queries) {
        Map<String, String> queryMap = new HashMap<>();
        if (!queries.isEmpty()) {
            String[] queryArr = queries.split("&");
            for (String query : queryArr) {
                String[] splitQuery = query.split("=");
                queryMap.put(splitQuery[0], splitQuery[1]);
            }
        }
        return queryMap;
    }

    public String getUri() {
        return uri;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getQueryMap() {
        return queryMap;
    }
}
