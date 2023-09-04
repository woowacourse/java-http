package org.apache.coyote.http.request;

import org.apache.coyote.http.util.HttpMethod;
import org.apache.coyote.http.util.HttpVersion;

public class Request {

    private final HttpRequestHeaders headers;
    private final HttpMethod method;
    private final HttpVersion version;
    private final Url url;
    private final HttpRequestBody body;
    private final QueryParameters queryParameters;

    public Request(
            final HttpRequestHeaders headers,
            final HttpMethod method,
            final HttpVersion version,
            final Url url,
            final HttpRequestBody body,
            final QueryParameters queryParameters
    ) {
        this.headers = headers;
        this.method = method;
        this.version = version;
        this.url = url;
        this.body = body;
        this.queryParameters = queryParameters;
    }

    public String findHeaderValue(final String headerKey) {
        return headers.findValue(headerKey);
    }

    public String findQueryParameterValue(final String queryParameterKey) {
        return queryParameters.findValue(queryParameterKey);
    }

    public boolean matchesByMethod(final HttpMethod method) {
        return this.method.matches(method);
    }

    public boolean matchesByPath(final String targetPath, final String rootContextPath) {
        return url.matchesByPath(targetPath, rootContextPath);
    }

    public boolean matchesByRootContextPath(final String rootContextPath) {
        return url.startsWithRootContextPath(rootContextPath);
    }

    public boolean isWelcomePageRequest(final String rootContextPath) {
        return url.isWelcomePageUrl(rootContextPath);
    }

    public boolean isStaticResource() {
        return url.isStaticResource();
    }

    public boolean hasQueryParameters() {
        return queryParameters.size() > 0;
    }

    public HttpVersion version() {
        return version;
    }

    public String url() {
        return url.url();
    }

    public String resourceName() {
        return url.resourceName();
    }
}
