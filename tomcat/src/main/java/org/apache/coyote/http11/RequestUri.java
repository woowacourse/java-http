package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RequestUri {
    private static final Pattern REQUEST_URI_PATTERN = Pattern.compile("GET (.+?) ");

    private final String path;
    private final Map<String, String> queryParams;

    private RequestUri(final String path, final Map<String, String> queryParams) {
        this.path = path;
        this.queryParams = queryParams;
    }

    public static RequestUri from(final String request) {
        final Matcher requestUriMatcher = REQUEST_URI_PATTERN.matcher(request);
        final Map<String, String> queryParams = new HashMap<>();
        String path = "";

        if (requestUriMatcher.find()) {
            final String uri = requestUriMatcher.group(1);
            int index = uri.indexOf("?");
            if (index != -1) {
                path = uri.substring(0, index);
                final StringTokenizer queryParam = new StringTokenizer(uri.substring(index + 1), "&");
                while (queryParam.hasMoreTokens()) {
                    final String param = queryParam.nextToken();
                    final String[] split = param.split("=");
                    queryParams.put(split[0], split[1]);
                }

            } else {
                path = uri;
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
