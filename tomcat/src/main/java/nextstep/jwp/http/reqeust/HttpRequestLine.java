package nextstep.jwp.http.reqeust;

import java.util.Map;

public class HttpRequestLine {

    private static final String QUERY_IDENTIFIER = "?";
    private static final String HTML_EXTENSION = ".html";

    private String method;
    private String path;
    private QueryParams queryParams;
    private String version;

    private HttpRequestLine(final String method, final String path, final String queryString, final String version) {
        this.method = method;
        this.path = path;
        this.queryParams = new QueryParams(queryString);
        this.version = version;
    }

    public static HttpRequestLine from(final String method, final String url, final String version) {
        String path = extractPath(url);
        String queryString = extractQueryString(url);
        return new HttpRequestLine(method, path, queryString, version);
    }

    private static String extractPath(final String url) {
        int index = url.indexOf(QUERY_IDENTIFIER);
        return url.substring(0, index) + HTML_EXTENSION;
    }

    private static String extractQueryString(final String url) {
        int index = url.indexOf(QUERY_IDENTIFIER);
        return url.substring(index + 1);
    }

    public boolean hasQueryParams() {
        return queryParams.isNotEmpty();
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getQueryParams() {
        return queryParams.getParams();
    }
}
