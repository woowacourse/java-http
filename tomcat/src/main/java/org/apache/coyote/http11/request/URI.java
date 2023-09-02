package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;

public class URI {

    private final String uri;
    private final String path;
    private final Map<String, String> queryStrings;

    private URI(String uri, String path, Map<String, String> queryStrings) {
        this.uri = uri;
        this.path = path;
        this.queryStrings = queryStrings;
    }

    public static URI from(String uri) {
        String[] split = uri.split("\\?");

        return new URI(uri, split[0], queryStrings(uri));
    }

    private static Map<String, String> queryStrings(String uri) {
        int start = uri.indexOf("?");
        Map<String, String> result = new HashMap<>();
        if (start == -1) {
            return result;
        }
        String queryStrings = uri.substring(uri.indexOf("?") + 1);
        String[] strings = queryStrings.split("&");
        for (String string : strings) {
            String[] nameAndValue = string.split("=");
            result.put(nameAndValue[0], nameAndValue[1]);
        }
        return result;
    }

    public String uri() {
        return uri;
    }

    public String path() {
        return path;
    }

    public Map<String, String> queryStrings() {
        return queryStrings;
    }
}
