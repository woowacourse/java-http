package org.apache.catalina;

import java.util.HashMap;
import java.util.Map;

public class RequestLine {

    public static final String QUERY_INDICATOR = "?";
    public static final String QUERY_COMPONENT_DELIMITER = "&";
    public static final String QUERY_COMPONENT_VALUE_DELIMITER = "=";

    private final HttpMethod httpMethod;
    private final String path;
    private final String VersionOfProtocol;
    private final Map<String, String> queryParams;

    public RequestLine(String requestLine) {
        String[] requestLineEntries = requestLine.split(" ");

        this.httpMethod = HttpMethod.valueOf(requestLineEntries[0]);
        this.path = requestLineEntries[1];
        this.VersionOfProtocol = requestLineEntries[2];
        this.queryParams = mapQueryParam();
    }

    public boolean isMethod(HttpMethod httpMethod) { // TODO: 상수화
        return this.httpMethod == httpMethod;
    }

    public String getPath() {
        return path;
    }

    public boolean hasQueryParam() {
        return path.contains("?");
    }

    public String getQueryParam(String paramName) {
        return queryParams.get(paramName);
    }

    private Map<String, String> mapQueryParam() {
        Map<String, String> mappedQueryParams = new HashMap<>();
        if (!path.contains("?")) {
            return mappedQueryParams;
        }

        int queryParamIndex = path.indexOf(QUERY_INDICATOR);
        String queryString = path.substring(queryParamIndex + 1);
        String[] splittedQueryString = queryString.split(QUERY_COMPONENT_DELIMITER);

        for (String queryParamEntry : splittedQueryString) {
            mappedQueryParams.put(
                    queryParamEntry.split(QUERY_COMPONENT_VALUE_DELIMITER)[0],
                    queryParamEntry.split(QUERY_COMPONENT_VALUE_DELIMITER)[1]
            );
        }
        return mappedQueryParams;
    }

    public ContentType getContentType() {
        return ContentType.findByPath(path);
    }

    public boolean isStaticRequest() {
        return ContentType.isStaticFile(path);
    }

    public boolean isPath(String path) {
        return this.path.equals(path);
    }

    public boolean isPathWithQuery(String path) {
        return isPath(path) && hasQueryParam();
    }
}
