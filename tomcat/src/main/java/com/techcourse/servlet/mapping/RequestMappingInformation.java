package com.techcourse.servlet.mapping;

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

    public boolean matches(HttpServletRequest request) {
        return request.methodEquals(httpMethod) &&
                request.uriEquals(uri) &&
                request.protocolEquals(httpProtocol);
    }
}
