package org.apache.coyote.http11.request.spec;

import java.util.Map;

public class RequestUrl {

    private static final int PATH_INDEX = 0;
    private static final int QUERY_STRING_INDEX = 1;
    private static final String PATH_QUERY_STRING_DELIMITER = "\\?";

    private String path;
    private final QueryParams queryParams;

    public RequestUrl(String path, QueryParams queryParams) {
        this.path = path;
        this.queryParams = queryParams;
    }

    public static RequestUrl from(String url) {
        String[] components = url.split(PATH_QUERY_STRING_DELIMITER);
        if (components.length < 2) {
            return new RequestUrl(components[PATH_INDEX], QueryParams.empty());
        }
        return new RequestUrl(components[PATH_INDEX], QueryParams.from(components[QUERY_STRING_INDEX]));
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

    public Map<String, String> getParams() {
        return queryParams.getParams();
    }

    public void setPath(String pathName) {
        this.path = pathName;
    }
}
