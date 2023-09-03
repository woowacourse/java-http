package org.apache.coyote.http.request.exception;

import org.apache.coyote.http.request.HttpRequestBody;
import org.apache.coyote.http.request.HttpRequestHeaders;
import org.apache.coyote.http.request.QueryParameters;
import org.apache.coyote.http.request.Url;
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

    public boolean matchesByPath(final String targetPath, final String contextRoot) {
        return url.matchesByPath(targetPath, contextRoot);
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
}
