package org.apache.coyote.request;

import org.apache.coyote.request.query.QueryParams;

public class HttpRequestBody {

    private final String requestBody;

    private HttpRequestBody(final String requestBody) {
        this.requestBody = requestBody;
    }

    public static HttpRequestBody from(final String requestBody) {
        return new HttpRequestBody(requestBody);
    }

    public QueryParams getBodyWithQueryParam() {
        return QueryParams.from(requestBody);
    }
}
