package nextstep.jwp.http.reqeust;

import java.util.Map;

public class HttpRequestLine {

    private static final String QUERY_IDENTIFIER = "?";
    private static final String HTML_EXTENSION = ".html";
    private static final String LINE_SEPARATOR = " ";
    private static final int METHOD_INDEX = 0;
    private static final int URL_INDEX = 1;
    private static final int VERSION_INDEX = 2;

    private String method;
    private String url;
    private QueryParams queryParams;
    private String version;

    private HttpRequestLine(final String method, final String url, final String version) {
        this.method = method;
        this.url = url;
        this.queryParams = new QueryParams(url);
        this.version = version;
    }

    public static HttpRequestLine from(final String requestLine) {
        String[] requestLines = requestLine.split(LINE_SEPARATOR);
        return new HttpRequestLine(requestLines[METHOD_INDEX], requestLines[URL_INDEX], requestLines[VERSION_INDEX]);
    }

    public boolean hasQueryString() {
        return queryParams.isNotEmpty();
    }

    public String getUrl() {
        if (url.contains(QUERY_IDENTIFIER)) {
            int index = url.indexOf(QUERY_IDENTIFIER);
            return url.substring(0, index) + HTML_EXTENSION;
        }
        return url;
    }

    public Map<String, String> getQueryParams() {
        return queryParams.getParams();
    }
}
