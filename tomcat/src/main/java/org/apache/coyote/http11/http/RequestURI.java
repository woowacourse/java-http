package org.apache.coyote.http11.http;

public class RequestURI {

    private final String requestURI;

    private RequestURI(final String requestURI) {
        this.requestURI = requestURI;
    }

    public static RequestURI from(final String requestURI) {
        if ("/".equals(requestURI)) {
            return new RequestURI("/index.html");
        }
        return new RequestURI(requestURI);
    }

    public boolean contains(final String login) {
        return requestURI.contains(login);
    }

    public String getRequestURI() {
        return requestURI;
    }
}
