package org.apache.coyote.domain;

import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    private final String uri;
    private final Map<String, String> queryParam;

    private HttpRequest(String uri, Map<String, String> queryParam) {
        this.uri = uri;
        this.queryParam = queryParam;
    }

    public static HttpRequest from(String headerFirstLine) {
        String[] headers = headerFirstLine.split(" ");
        Map<String, String> queryParam = new HashMap<>();
        if (headers[1].contains("?")) {
            String[] uriPaths = headers[1].split("\\?");
            String queryString = uriPaths[1];
            String[] queryParams = queryString.split("&");
            for (String query : queryParams) {
                String[] splitQuery = query.split("=");
                queryParam.put(splitQuery[0], splitQuery[1]);
            }
        }
        return new HttpRequest(headers[1], queryParam);
    }

    public String getUri() {
        return uri;
    }

    public Map<String, String> getQueryParam() {
        return queryParam;
    }
}
