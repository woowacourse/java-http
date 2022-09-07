package org.apache.coyote.domain.request.requestline;

public class Path {

    private final String path;
    private final QueryParam queryParam;

    private Path(String path, QueryParam queryParam) {
        this.path = path;
        this.queryParam = queryParam;
    }

    public static Path from(String url) {
        String[] values = url.split("\\?");
        String path = values[0];
        String queryString = getQueryString(values);
        QueryParam queryParam = QueryParam.from(queryString);
        return new Path(path, queryParam);
    }

    private static String getQueryString(String[] values) {
        if (values.length == 1) {
            return "";
        }
        return values[1];
    }

    public String getPath() {
        return path;
    }

    public QueryParam getQueryParam() {
        return queryParam;
    }
}
