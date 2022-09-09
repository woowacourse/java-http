package org.apache.coyote.http11.model;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public class HttpRequestURI {

    private static final String DEFAULT_PATH = "/hello";
    private static final String QUERY_PARAM_DELIMITER = "?";
    private static final String EXTENSION_DELIMITER = ".";

    private final String path;
    private final Map<String, String> queryParams;

    private HttpRequestURI(String path, Map<String, String> queryParams) {
        this.path = path;
        this.queryParams = queryParams;
    }

    public static HttpRequestURI from(String uri) {
        String path = uri;
        Map<String, String> queryParams = new HashMap<>();

        if (uri.contains(QUERY_PARAM_DELIMITER)) {
            int index = uri.indexOf(QUERY_PARAM_DELIMITER);
            path = uri.substring(0, index);

            String queryString = uri.substring(index + 1);
            String[] queries = queryString.split("&");
            for (String query : queries) {
                String[] queryEntry = query.split("=");
                queryParams.put(queryEntry[0], queryEntry[1]);
            }
        }

        if (path.equals("/")) {
            path = DEFAULT_PATH;
        }

        if (!path.contains(EXTENSION_DELIMITER)) {
            path = path + ".html";
        }

        return new HttpRequestURI(path, queryParams);
    }

    public String getPath() {
        return path;
    }

    public String getExtension() {
        return StringUtils.substringAfterLast(path, EXTENSION_DELIMITER);
    }
}
