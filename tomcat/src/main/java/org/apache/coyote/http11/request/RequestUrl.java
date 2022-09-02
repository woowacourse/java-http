package org.apache.coyote.http11.request;

public class RequestUrl {

    private final String path;
    private final QueryParams queryParams;

    public RequestUrl(String path, QueryParams queryParams) {
        this.path = path;
        this.queryParams = queryParams;
    }

    public static RequestUrl from(String url) {
        String[] components = url.split("\\?");
        if (components.length < 2) {
            return new RequestUrl(components[0], QueryParams.empty());
        }
        return new RequestUrl(components[0], QueryParams.from(components[1]));
    }

    public boolean hasQueryParams() {
        return queryParams.hasParams();
    }

    public String getPath() {
        return path;
    }

    public QueryParams getQueryParams() {
        return queryParams;
    }
}
