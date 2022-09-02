package nextstep.jwp.http.reqeust;

import java.util.Map;

public class HttpRequestLine {

    private String method;
    private String url;
    private QueryString queryString;
    private String version;

    private HttpRequestLine(final String method, final String url, final String version) {
        this.method = method;
        this.url = url;
        this.queryString = new QueryString(url);
        this.version = version;
    }

    public static HttpRequestLine from(final String requestLine) {
        String[] requestLines = requestLine.split(" ");
        return new HttpRequestLine(requestLines[0], requestLines[1], requestLines[2]);
    }

    public boolean hasQueryString() {
        return queryString.isNotEmpty();
    }

    public String getUrl() {
        if (url.contains("?")) {
            int index = url.indexOf("?");
            return url.substring(0, index) + ".html";
        }
        return url;
    }

    public Map<String, String> getQueryString() {
        return queryString.getQueryString();
    }
}
