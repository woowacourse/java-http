package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class RequestUri {
    private final String path;
    private final Map<String, String> queryParams;

    private RequestUri(final String path, final Map<String, String> queryParams) {
        this.path = path;
        this.queryParams = queryParams;
    }

    public static RequestUri from(final String requestUri) {
        final Map<String, String> queryParams = new HashMap<>();
        int index = requestUri.indexOf("?");
        String path = requestUri;
        if (index != -1) {
            path = requestUri.substring(0, index);
            final StringTokenizer token = new StringTokenizer(requestUri.substring(index + 1), "&");
            while (token.hasMoreTokens()) {
                final String param = token.nextToken();
                final String[] split = param.split("=");
                queryParams.put(split[0], split[1]);
            }
        }

        return new RequestUri(path, queryParams);
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }
}
