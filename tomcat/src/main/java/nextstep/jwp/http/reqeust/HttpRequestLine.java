package nextstep.jwp.http.reqeust;

import java.net.URI;
import java.util.Map;

public class HttpRequestLine {

    private static final String HTML_EXTENSION = ".html";

    private String method;
    private String path;
    private QueryParams queryParams;
    private String version;

    public HttpRequestLine(final String method, final URI uri, final String version) {
        this.method = method;
        this.path = uri.getPath();
        this.queryParams = new QueryParams(uri.getQuery());
        this.version = version;
    }

    public boolean hasQueryParams() {
        return queryParams.isNotEmpty();
    }

    private boolean hasNotExtension() {
        return !path.contains(".");
    }

    private boolean isNotRootPath() {
        return !path.equals("/");
    }

    public String getPath() {
        if (hasNotExtension() && isNotRootPath()) {
            return path + HTML_EXTENSION;
        }
        return path;
    }

    public Map<String, String> getQueryParams() {
        return queryParams.getParams();
    }

    public String getMethod() {
        return method;
    }
}
