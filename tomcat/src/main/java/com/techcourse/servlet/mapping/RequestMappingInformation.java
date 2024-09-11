package com.techcourse.servlet.mapping;

import java.util.Objects;
import org.apache.coyote.http11.HttpProtocol;
import org.apache.coyote.http11.request.HttpServletRequest;
import org.apache.coyote.http11.request.line.Method;
import org.apache.coyote.http11.request.line.Uri;

public class RequestMappingInformation {

    private final Method httpMethod;
    private final Uri uri;
    private final HttpProtocol httpProtocol;

    public RequestMappingInformation(Method httpMethod, String uri, HttpProtocol httpProtocol) {
        this.httpMethod = httpMethod;
        this.uri = new Uri(uri);
        this.httpProtocol = httpProtocol;
    }

    public static RequestMappingInformation from(HttpServletRequest request) {
        return new RequestMappingInformation(request.getMethod(), request.getUriPath(), request.getProtocol());
    }

    public boolean matches(HttpServletRequest request) {
        return request.methodEquals(httpMethod) &&
                request.uriEquals(uri) &&
                request.protocolEquals(httpProtocol);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RequestMappingInformation that = (RequestMappingInformation) o;
        return httpMethod == that.httpMethod && Objects.equals(uri, that.uri)
                && httpProtocol == that.httpProtocol;
    }

    @Override
    public int hashCode() {
        return Objects.hash(httpMethod, uri, httpProtocol);
    }
}
