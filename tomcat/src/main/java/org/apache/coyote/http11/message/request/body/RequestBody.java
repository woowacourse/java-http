package org.apache.coyote.http11.message.request.body;

import org.apache.coyote.http11.message.request.QueryParams;

public class RequestBody {

    private final QueryParams queryParams;

    private RequestBody(final QueryParams queryParams) {
        this.queryParams = queryParams;
    }

    public static RequestBody from(String body) {
        return new RequestBody(QueryParams.from(body));
    }

    public static RequestBody ofEmpty() {
        return new RequestBody(QueryParams.ofEmpty());
    }

    public QueryParams getQueryParams() {
        return queryParams;
    }
}
