package org.apache.catalina.servlet.request;

import java.util.Collections;
import java.util.Map;
import org.apache.catalina.servlet.util.RequestParamUtil;

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
        if (start == -1) {
            return Collections.emptyMap();
        }
        String queryStrings = uri.substring(uri.indexOf("?") + 1);
        return RequestParamUtil.parse(queryStrings);
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
