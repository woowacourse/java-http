package org.apache.servlet;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public interface Servlet {

    boolean isSupported(final String path);

    void service(final HttpRequest httpRequest, final HttpResponse httpResponse);
}
