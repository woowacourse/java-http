package org.apache.coyote.servlet.servlets;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public interface Servlet {

    HttpResponse service(final HttpRequest httpRequest);
}
