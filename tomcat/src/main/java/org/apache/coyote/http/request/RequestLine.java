package org.apache.coyote.http.request;

import org.apache.coyote.http.util.HttpConsts;
import org.apache.coyote.http.util.HttpMethod;
import org.apache.coyote.http.util.HttpVersion;

public class RequestLine {

    private static final int START_LINE_METHOD_INDEX = 0;
    private static final int START_LINE_URL_INDEX = 1;
    private static final int START_LINE_HTTP_VERSION = 2;

    private final HttpMethod method;
    private final Url url;
    private final HttpVersion version;

    private RequestLine(final HttpMethod method, final Url url, final HttpVersion version) {
        this.method = method;
        this.url = url;
        this.version = version;
    }

    public static RequestLine from(final String requestLineContent) {
        final String[] requestLineTokens = requestLineContent.split(HttpConsts.SPACE);
        final HttpMethod method = HttpMethod.findMethod(requestLineTokens[START_LINE_METHOD_INDEX]);
        final Url url = Url.from(requestLineTokens[START_LINE_URL_INDEX]);
        final HttpVersion httpVersion = HttpVersion.findVersion(requestLineTokens[START_LINE_HTTP_VERSION]);

        return new RequestLine(method, url, httpVersion);
    }

    public boolean matchesByMethod(final HttpMethod method) {
        return this.method.matches(method);
    }

    public boolean matchesByPathExcludingContextPath(final String targetPath, final String contextPath) {
        return url.matchesByPathExcludingContextPath(targetPath, contextPath);
    }

    public boolean matchesByContextPath(final String contextPath) {
        return url.startsWithContextPath(contextPath);
    }

    public boolean isWelcomePageRequest(final String contextPath) {
        return url.isWelcomePageUrl(contextPath);
    }

    public boolean isStaticResource() {
        return url.isStaticResource();
    }

    public HttpMethod method() {
        return method;
    }

    public Url url() {
        return url;
    }

    public HttpVersion version() {
        return version;
    }
}
