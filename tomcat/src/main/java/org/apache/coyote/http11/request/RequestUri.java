package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;

public class RequestUri {

    private static final String HTML_URI = ".html";
    private static final String CSS_URI = ".css";
    private static final String JAVASCRIPT_URI = ".js";

    private final String value;

    public RequestUri(final String value) {
        this.value = value;
    }

    public boolean isHtmlUri() {
        return value.endsWith(HTML_URI);
    }

    public boolean isCssUri() {
        return value.endsWith(CSS_URI);
    }

    public boolean isJavaScriptUri() {
        return value.endsWith(JAVASCRIPT_URI);
    }

    public Map<String, Object> parseQueryParams() {
        Map<String, Object> paramMap = new HashMap<>();

        int index = value.indexOf("?");
        String queryString = value.substring(index + 1);
        String[] params = queryString.split("&");
        for (String param : params) {
            String[] paramPair = param.split("=");
            paramMap.put(paramPair[0], paramPair[1]);
        }
        return paramMap;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "RequestUri{" +
                "value='" + value + '\'' +
                '}';
    }
}
