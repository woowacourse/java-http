package org.apache.coyote.http.request;

import java.util.Map;
import org.apache.coyote.http.HttpVersion;

public class HttpRequestLine {

    private RequestMethod method;
    private RequestPath path;
    private HttpVersion version;

    private HttpRequestLine(final RequestMethod method, final RequestPath path, final HttpVersion version) {

        this.method = method;
        this.path = path;
        this.version = version;
    }

    public static HttpRequestLine from(final RequestMethod method, final String url, final HttpVersion version) {
        RequestPath path = RequestPath.from(url);
        return new HttpRequestLine(method, path, version);
    }

    public boolean hasQueryParams() {
        return path.hasQueryParams();
    }

    public String getPath() {
        return path.getPath();
    }

    public Map<String, String> getQueryParams() {
        return path.getQueryParams();
    }

    public HttpVersion getVersion() {
        return version;
    }
}
