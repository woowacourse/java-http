package org.apache.coyote.http11.model;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public class HttpURI {

    private static final String DEFAULT_PATH = "/index";

    private String path;
    private Map<String, String> queryParams;

    private HttpURI(String path, Map<String, String> queryParams) {
        this.path = path;
        this.queryParams = queryParams;
    }

    public static HttpURI from(String uri) {
        String path = uri;
        Map<String, String> queryParams = new HashMap<>();

        if (uri.contains("?")) {
            int index = uri.indexOf("?");
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

        if (!path.contains(".")) {
            path = path + ".html";
        }

        return new HttpURI(path, queryParams);
    }

    public boolean pathStartsWith(String text) {
        return path.startsWith(text);
    }

    public String getPath() {
        return path;
    }

    public String getExtension() {
        return StringUtils.substringAfterLast(path, ".");
    }
}
