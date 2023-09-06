package org.apache.coyote.http11.request;

import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class HttpRequestUri {

    private static final String URI_DELIMITER = "\\?";
    public static final int KEY_INDEX = 0;
    public static final int VALUE_INDEX = 1;
    public static final String QUERY_ENTRY_DELIMITER = "&";
    public static final String KEY_VALUE_DELIMITER = "=";

    private final String path;
    private final Map<String, String> queryString;


    private HttpRequestUri(final String path, final Map<String, String> queryString) {
        this.path = path;
        this.queryString = queryString;
    }

    public static HttpRequestUri from(String uri) {
        if (!uri.contains(URI_DELIMITER)) {
            return new HttpRequestUri(uri, null);
        }
        return parseQueryString(uri);
    }

    private static HttpRequestUri parseQueryString(String uri) {
        String[] uriElements = uri.split(URI_DELIMITER);
        String path = uriElements[KEY_INDEX];

        Map<String, String> queryString = Pattern.compile(QUERY_ENTRY_DELIMITER)
                .splitAsStream(uriElements[VALUE_INDEX].trim())
                .map(queryEntry -> queryEntry.split(KEY_VALUE_DELIMITER))
                .collect(Collectors.toUnmodifiableMap(query -> query[KEY_INDEX], query -> query[VALUE_INDEX]));

        return new HttpRequestUri(path, queryString);
    }

    public boolean containsPath(String path) {
        return this.path.contains(path);
    }

    public boolean samePath(String path) {
        return this.path.equals(path);
    }

    public boolean hasQueryString() {
        return this.queryString != null;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getQueryString() {
        return queryString;
    }
}
