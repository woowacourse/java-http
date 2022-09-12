package org.apache.coyote.http11.request;

import java.util.Map;

public class RequestLine {

    private static final String QUERY_DELIMITER = "?";

    private final HttpMethod httpMethod;
    private final String path;
    private final QueryParams queryParams;

    public RequestLine(String[] startLine) {
        httpMethod = HttpMethod.from(startLine[0]);
        String url = startLine[1];
        path = url.split("\\" + QUERY_DELIMITER)[0];
        queryParams = new QueryParams(url);
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getQueryParams() {
        return queryParams.getQueryParams();
    }
}
