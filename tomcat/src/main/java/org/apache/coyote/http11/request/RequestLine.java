package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.util.StringUtils;

public class RequestLine {

    private static final String START_LINE_DELIMITER = " ";
    private static final String QUERY_STRING_DELIMITER = "?";

    private final HttpMethod httpMethod;
    private final String httpUrl;
    private Map<String, String> queryParams = new HashMap<>();

    public RequestLine(String startLine) {
        String[] startLines = startLine.split(START_LINE_DELIMITER);
        httpMethod = HttpMethod.from(startLines[0]);
        httpUrl = parseUrlWithQueryString(startLines[1]);
    }

    private String parseUrlWithQueryString(String url) {
        if (hasQueryString(url)) {
            parseQueryParams(url);
            return rejectQueryStringFromUrl(url);
        }
        return url;
    }

    private boolean hasQueryString(String url) {
        return url.contains(QUERY_STRING_DELIMITER);
    }

    private void parseQueryParams(String url) {
        String queryString = url.split("\\?")[1];
        queryParams = StringUtils.parseKeyAndValues(queryString);
    }

    private String rejectQueryStringFromUrl(String url) {
        int queryStringDelimiterIndex = url.indexOf(QUERY_STRING_DELIMITER);
        return url.substring(0, queryStringDelimiterIndex);
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getHttpUrl() {
        return httpUrl;
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }
}
