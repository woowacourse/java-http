package org.apache.coyote.http.request;

import org.apache.coyote.http.HttpVersion;

public class HttpRequestLine {

    private final RequestMethod method;
    private final RequestPath path;
    private final HttpVersion version;

    private HttpRequestLine(final RequestMethod method, final RequestPath path, final HttpVersion version) {
        this.method = method;
        this.path = path;
        this.version = version;
    }

    public static HttpRequestLine from(final RequestMethod method, final String url, final HttpVersion version) {
        RequestPath path = RequestPath.from(url);
        return new HttpRequestLine(method, path, version);
    }

    public String getPath() {
        return path.getPath();
    }

    public boolean isGet() {
        return method.equals(RequestMethod.GET);
    }
}
