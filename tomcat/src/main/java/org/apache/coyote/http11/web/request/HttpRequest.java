package org.apache.coyote.http11.web.request;

import org.apache.coyote.http11.file.ResourceExtension;
import org.apache.coyote.http11.support.HttpHeaders;
import org.apache.coyote.http11.support.HttpMethod;
import org.apache.coyote.http11.support.HttpStartLine;
import org.apache.coyote.http11.web.QueryParameters;
import java.util.Objects;

public class HttpRequest {

    private final HttpStartLine httpStartLine;
    private final HttpHeaders httpHeaders;
    private final String requestBody;

    public HttpRequest(final HttpStartLine httpStartLine, final HttpHeaders httpHeaders, final String requestBody) {
        this.httpStartLine = httpStartLine;
        this.httpHeaders = httpHeaders;
        this.requestBody = requestBody;
    }

    public boolean isStaticResource() {
        final String uri = httpStartLine.getUri();
        return ResourceExtension.contains(uri);
    }

    public HttpHeaders getHeaders() {
        return httpHeaders;
    }

    public QueryParameters getQueryParameters() {
        return httpStartLine.getQueryParameters();
    }

    public boolean isMethod(final HttpMethod httpMethod) {
        return httpStartLine.getHttpMethod() == httpMethod;
    }

    public boolean isUri(final String uri) {
        return httpStartLine.getUri().equals(uri);
    }

    public String getUri() {
        return httpStartLine.getUri();
    }

    public String getRequestBody() {
        return requestBody;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof HttpRequest)) return false;
        final HttpRequest that = (HttpRequest) o;
        return Objects.equals(httpStartLine, that.httpStartLine) && Objects.equals(httpHeaders, that.httpHeaders) && Objects.equals(requestBody, that.requestBody);
    }

    @Override
    public int hashCode() {
        return Objects.hash(httpStartLine, httpHeaders, requestBody);
    }

    @Override
    public String toString() {
        return "HttpRequest{" +
                "httpStartLine=" + httpStartLine +
                ", httpHeaders=" + httpHeaders +
                ", requestBody='" + requestBody + '\'' +
                '}';
    }
}
