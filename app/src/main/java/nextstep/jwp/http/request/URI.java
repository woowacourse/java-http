package nextstep.jwp.http.request;

import java.util.HashMap;
import java.util.Map;

public class URI {

    private final String path;
    private final Map<String, String> queryString;

    private URI(String path, Map<String, String> queryString) {
        this.path = path;
        this.queryString = queryString;
    }

    public static URI of(String uri) {
        HashMap<String, String> queryString = new HashMap<>();
        int index = uri.indexOf("?");
        if (index < 0) {
            return new URI(uri, null);
        }
        String path = uri.substring(0, index);
        String queries = uri.substring(index + 1);
        if (!queries.isEmpty()) {
            String[] queryStrings = queries.split("&");
            for (String query : queryStrings) {
                String[] values = query.split("=");
                queryString.put(values[0], values[1]);
            }
        }
        return new URI(path, queryString);
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getQueryString() {
        return queryString;
    }

    public String getQueryParameter(String key) {
        return queryString.get(key);
    }
}
