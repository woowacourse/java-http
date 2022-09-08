package org.apache.coyote.http11;

import org.apache.coyote.http11.util.StaticResourceExtensionSupporter;

public class RequestURI {


    private final String requestURI;

    public RequestURI(final String requestURI) {
        this.requestURI = requestURI;
    }

    public static RequestURI from(final String requestURI) {
        if ("/".equals(requestURI)) {
            return new RequestURI("/index.html");
        }
        return new RequestURI(requestURI);
    }

    public boolean isStaticResource() {
        return StaticResourceExtensionSupporter.isStaticResource(requestURI);
    }

    public boolean contains(final String login) {
        return requestURI.contains(login);
    }

    public String getRequestURI() {
        return requestURI;
    }
}
