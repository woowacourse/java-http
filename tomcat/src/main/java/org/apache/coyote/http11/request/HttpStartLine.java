package org.apache.coyote.http11.request;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.coyote.http11.constant.HttpMethod;

public class HttpStartLine {

    private final HttpMethod method;
    private final String url;
    private final String version;

    public HttpStartLine(String method, String url) {
        this.method = HttpMethod.toHttpMethod(method);
        this.url = url;
        this.version = "HTTP/1.1";
    }

    public String getUrl() {
        int queryIndex = url.indexOf("?");
        if (queryIndex == -1) {
            queryIndex = url.length();
        }

        return url.substring(0, queryIndex);
    }

    public Map<String, String> getQueryString() {
        int queryIndex = url.indexOf("?");
        if (queryIndex == -1) {
            return Collections.emptyMap();
        }

        Map<String, String> queries = new HashMap<>();
        String rawQuery = url.substring(queryIndex + 1);
        for (String query : rawQuery.split("&")) {
            String[] temp = query.split("=");
            queries.put(temp[0].toLowerCase(), temp[1].toLowerCase());
        }

        return queries;
    }

    public HttpMethod getMethod() {
        return method;
    }
}
