package org.apache.coyote.request.requestLine;

import java.util.HashMap;
import java.util.Map;

public class RequestPath {

    public static final char QUERY_PARAM_SEPARATOR = '?';
    public static final String QUERY_PARAM_SPLIT = "&";
    public static final String QUERY_PARAM_GET_MAPPER = "=";

    private String requestPath;
    private Map<String, String> queryParams;

    public static RequestPath from(final String requestPath) {
        int queryIndex = requestPath.indexOf(QUERY_PARAM_SEPARATOR);
        if(queryIndex >= 0){
            String path = requestPath.substring(0, queryIndex);
            String query = requestPath.substring(queryIndex + 1);
            return new RequestPath(path, query);
        }
        return new RequestPath(requestPath);
    }

    private RequestPath(final String requestPath) {
        this.requestPath = requestPath;
        this.queryParams = new HashMap<>();
    }

    private RequestPath(final String requestPath, final String queryParams) {
        this(requestPath);

        String[] queries = queryParams.split(QUERY_PARAM_SPLIT);
        for (String query : queries) {
            final String[] split = query.split(QUERY_PARAM_GET_MAPPER);

            this.queryParams.put(split[0], split[1]);
        }
    }

    public String getRequestPath() {
        return requestPath;
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }

    public boolean isSame(final String requestPath) {
        return this.requestPath.equals(requestPath);
    }

    public String getRequestPathExtension() {
        if (!requestPath.contains(".")) {
            throw new IllegalArgumentException("[ERROR] 확장자를 찾을 수 없습니다.");
        }
        return requestPath.split("\\.")[1];
    }
}
