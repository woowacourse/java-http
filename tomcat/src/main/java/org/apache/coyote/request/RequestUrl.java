package org.apache.coyote.request;

public class RequestUrl {
    private final String path;
    private final QueryString queryString;

    public RequestUrl(String path, QueryString queryString) {
        this.path = path;
        this.queryString = queryString;
    }

    public static RequestUrl from(final String url) {
        final String path = getPath(url);
        final QueryString query = QueryString.from(url.substring(path.length()));
        return new RequestUrl(path, query);
    }

    private static String getPath(String url) {
        if(url.isBlank()){
            return "/";
        }
        return url.split("\\?")[0];
    }

    @Override
    public String toString() {
        return path + queryString;
    }
}
