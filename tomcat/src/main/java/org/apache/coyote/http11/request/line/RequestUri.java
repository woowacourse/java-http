package org.apache.coyote.http11.request.line;

import org.apache.coyote.http11.request.vo.QueryParams;

import java.net.URI;

public class RequestUri {

    private final String path;
    private final QueryParams queryParams;

    public RequestUri(String uri) {
        URI parsedUri = URI.create(uri);
        this.path = parsedUri.getPath();
        this.queryParams = new QueryParams(parsedUri.getRawQuery());
    }

    public String getPath() {
        return path;
    }

    public QueryParams getQueryParams() {
        return queryParams;
    }

}
