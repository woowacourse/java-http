package org.apache.coyote.domain.request.requestline;

public class Path {

    private static final String QUERY_STRING_REGEX = "\\?";
    private static final String EMPTY_QUERY_STRING = "";
    private static final String EXTENSION_REGEX = ".";
    private static final String HOME_URI = "/";
    private static final String HTML_EXTENSION = ".html";
    private static final int PATH_INDEX = 0;
    private static final int QUERY_STRING_INDEX = 1;
    private static final int ONLY_PATH_EXIST = 1;

    private final String uri;
    private final QueryParam queryParam;

    private Path(String uri, QueryParam queryParam) {
        this.uri = uri;
        this.queryParam = queryParam;
    }

    public static Path from(String url) {
        String[] values = url.split(QUERY_STRING_REGEX);
        String path = values[PATH_INDEX];
        String queryString = getQueryString(values);
        QueryParam queryParam = QueryParam.from(queryString);
        return new Path(path, queryParam);
    }

    private static String getQueryString(String[] values) {
        if (values.length == ONLY_PATH_EXIST) {
            return EMPTY_QUERY_STRING;
        }
        return values[QUERY_STRING_INDEX];
    }

    public String getUri() {
        return uri;
    }

    public QueryParam getQueryParam() {
        return queryParam;
    }
    
    public String getFilePath(){
        if (!uri.contains(EXTENSION_REGEX) && !uri.equals(HOME_URI)) {
            return uri +HTML_EXTENSION;
        }
        return uri;
    }
}
