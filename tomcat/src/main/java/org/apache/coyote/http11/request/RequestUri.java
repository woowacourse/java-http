package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.common.FileExtension;

public class RequestUri {

    private static final String QUERY_PARAM_START_VALUE = "?";

    private final String value;

    public RequestUri(final String value) {
        this.value = value;
    }

    public boolean isHtmlUri() {
        return value.endsWith(FileExtension.HTML.getValue());
    }

    public boolean isCssUri() {
        return value.endsWith(FileExtension.CSS.getValue());
    }

    public boolean isJavaScriptUri() {
        return value.endsWith(FileExtension.JAVASCRIPT.getValue());
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

    public boolean isContainsQueryParam() {
        return value.contains(QUERY_PARAM_START_VALUE);
    }

    public String getRemovedQueryParamUri() {
        int queryParamIndex = value.indexOf("?");
        return value.substring(0, queryParamIndex);
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
