package org.apache.coyote.request;

import org.apache.coyote.common.HttpVersion;

public class RequestStartLine {

    private final RequestMethod requestMethod;
    private final RequestUrl requestUrl;
    private final HttpVersion httpVersion;

    private RequestStartLine(
            final RequestMethod requestMethod,
            final RequestUrl requestUrl,
            final HttpVersion httpVersion
    ) {
        this.requestMethod = requestMethod;
        this.requestUrl = requestUrl;
        this.httpVersion = httpVersion;
    }

    public static RequestStartLine from(final String startLine) {
        final String[] startLines = splitLine(startLine);
        final RequestMethod method =RequestMethod.get(startLines[0]);
        final RequestUrl url = RequestUrl.from(startLines[1]);
        final HttpVersion version = HttpVersion.get(startLines[2]);
        return new RequestStartLine(method, url, version);
    }

    private static String[] splitLine(final String firstLine) {
        return firstLine.split(" ");
    }

    @Override
    public String toString() {
        return String.format("%s %s %s ", requestMethod, requestUrl, httpVersion);
    }

}
