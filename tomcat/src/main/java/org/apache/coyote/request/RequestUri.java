package org.apache.coyote.request;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

public class RequestUri {

    private static final String SEPERATOR = "\\?";
    private static final String QUERY_SEPARATOR = "&";
    private static final String KEY_VALUE_SEPEARTOR = "=";
    private static final String DOT = ".";

    private final String path;
    private final String extension;
    private final Map<String, String> queryParams;

    public RequestUri(String path, String extension, Map<String, String> queryParams) {
        this.path = path;
        this.extension = extension;
        this.queryParams = queryParams;
    }

    public static RequestUri from(String requestUri) {
        URI uri = URI.create(requestUri);


        String path = uri.getPath();
        String query = uri.getQuery();
        String extension = getExtension(path);

        return new RequestUri(path, extension, parseQueryStrings(query));
    }

    private static String getExtension(String path) {
        int dotIndex = path.lastIndexOf(DOT);

        if (dotIndex < 0) {
            return "";
        }
        return path.substring(dotIndex + 1);
    }

    private static Map<String, String> parseQueryStrings(String queryStrings) {
        Map<String, String> queryParams = new LinkedHashMap<>();

        if (queryStrings == null) {
            return queryParams;
        }

        String[] splitedQueryParams = queryStrings.split(QUERY_SEPARATOR);

        for (String line : splitedQueryParams) {
            String[] keyValue = line.split(KEY_VALUE_SEPEARTOR);
            if (keyValue.length == 2) {
                queryParams.put(keyValue[0], keyValue[1]);
            }
        }
        return queryParams;
    }

    public String getPath() {
        return path;
    }

    public String getExtension() {
        return extension;
    }
}
