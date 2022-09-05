package org.apache.coyote.domain;

public class HttpRequest {

    private static final String HEADER_DELIMITER = " ";

    private final String uri;
    private final QueryParam queryParam;

    private HttpRequest(String uri, QueryParam queryParam) {
        this.uri = uri;
        this.queryParam = queryParam;
    }

    public static HttpRequest from(String headerFirstLine) {
        String[] headers = headerFirstLine.split(HEADER_DELIMITER);
        return new HttpRequest(headers[1], QueryParam.from(headers[1]));
    }

    public String getUri() {
        return uri;
    }

    public QueryParam getQueryParam() {
        return queryParam;
    }
}
