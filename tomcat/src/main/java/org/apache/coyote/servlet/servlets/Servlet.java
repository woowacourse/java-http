package org.apache.coyote.servlet.servlets;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public interface Servlet {

    void service(final HttpRequest httpRequest, final HttpResponse httpResponse);
}
