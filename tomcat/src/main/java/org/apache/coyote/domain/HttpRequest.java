package org.apache.coyote.domain;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.handler.QueryStringHandler;

public class HttpRequest {

    private static final String HEADER_DELIMITER = " ";

    private final String uri;
    private final Map<String, String> queryParam;

    private HttpRequest(String uri, Map<String, String> queryParam) {
        this.uri = uri;
        this.queryParam = queryParam;
    }

    public static HttpRequest from(String headerFirstLine) {
        String[] headers = headerFirstLine.split(HEADER_DELIMITER);
        Map<String, String> queryParam = QueryStringHandler.queryParams(headers[1]);
        return new HttpRequest(headers[1], queryParam);
    }

    public String getUri() {
        return uri;
    }

    public Map<String, String> getQueryParam() {
        return queryParam;
    }
}
