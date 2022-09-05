package org.apache.coyote.http11.message.request.requestline;

import org.apache.coyote.http11.message.Regex;
import org.apache.coyote.http11.message.request.QueryParams;

public class RequestLine {

    private final Method method;
    private final RequestUri uri;

    private RequestLine(final Method method, final RequestUri uri) {
        this.method = method;
        this.uri = uri;
    }

    public static RequestLine from(String requestLine) {
        final String[] splitLine = requestLine.split(Regex.BLANK.getValue());
        final Method method = Method.valueOf(splitLine[0]);
        final String uri = splitLine[1];
        return new RequestLine(method, RequestUri.from(uri));
    }

    public boolean isPath(String path) {
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
