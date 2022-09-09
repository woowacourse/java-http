package org.apache.coyote.http11.message.request.requestline;

import org.apache.coyote.http11.message.Regex;
import org.apache.coyote.http11.message.request.QueryParams;

public class RequestLine {

    private static final int INDEX_METHOD = 0;
    private static final int INDEX_URI = 1;

    private final Method method;
    private final RequestUri uri;

    private RequestLine(final Method method, final RequestUri uri) {
        this.method = method;
        this.uri = uri;
    }

    public static RequestLine from(final String requestLine) {
        final String[] splitLine = requestLine.split(Regex.BLANK.getValue());
        final Method method = Method.valueOf(splitLine[INDEX_METHOD]);
        final String uri = splitLine[INDEX_URI];
        return new RequestLine(method, RequestUri.from(uri));
    }

    public boolean isPath(final String path) {
        return uri.isPath(path);
    }

    public boolean isForResource() {
        return uri.isForResource();
    }

    public boolean isMethod(final Method method) {
        return this.method == method;
    }

    public String getPath() {
        return uri.getPath();
    }

    public QueryParams getQueryParams() {
        return uri.getQueryParams();
    }
}
