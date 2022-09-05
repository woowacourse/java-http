package org.apache.coyote.http11.request;

public class URI {

    private static final String PATH_SPLITTER = "?";

    private final String path;
    private final QueryParams queryParams;

    public URI(String uri) {
        this.path = getPath(uri);
        this.queryParams = new QueryParams(uri);
    }

    private String getPath(String uri) {
        if (uri.contains(PATH_SPLITTER)) {
            int index = uri.indexOf(PATH_SPLITTER);
            return uri.substring(0, index);
        }
        return uri;
    }

    public String getPath() {
        return path;
    }

    public QueryParams getQueryParams() {
        return queryParams;
    }
}
