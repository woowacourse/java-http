package org.apache.coyote.request;

import org.apache.coyote.common.HttpVersion;
import org.apache.coyote.common.PathUrl;

public class RequestStartLine {

    private final RequestMethod requestMethod;
    private final PathUrl pathUrl;
    private final HttpVersion httpVersion;

    private RequestStartLine(
            final RequestMethod requestMethod,
            final PathUrl pathUrl,
            final HttpVersion httpVersion
    ) {
        this.requestMethod = requestMethod;
        this.pathUrl = pathUrl;
        this.httpVersion = httpVersion;
    }

    public static RequestStartLine from(final String startLine) {
        final String[] startLines = splitLine(startLine);
        final RequestMethod method =RequestMethod.get(startLines[0]);
        final PathUrl url = PathUrl.from(startLines[1]);
        final HttpVersion version = HttpVersion.get(startLines[2]);
        return new RequestStartLine(method, url, version);
    }

    private static String[] splitLine(final String firstLine) {
        return firstLine.split(" ");
    }

    public boolean isStatic() {
        return pathUrl.hasExtension();
    }

    public String getFileType() {
        return pathUrl.getFileType();
    }

    public String getPath() {
        return pathUrl.getPath();
    }

    public PathUrl getRequestUrl() {
        return pathUrl;
    }

    public HttpVersion getHttpVersion() {
        return httpVersion;
    }

    public String getContentType() {
        return pathUrl.getContentType();
    }

    public String getQueryValueBy(final String key) {
        return pathUrl.getQueryValueBy(key);
    }

    public boolean isPost() {
        return requestMethod.isPost();
    }

    public boolean isGet() {
        return requestMethod.isGet();
    }

    @Override
    public String toString() {
        return String.format("%s %s %s ", requestMethod, pathUrl, httpVersion);
    }
}
