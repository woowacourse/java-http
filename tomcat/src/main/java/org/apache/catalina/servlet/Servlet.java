package org.apache.catalina.servlet;

import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;

public interface Servlet {

    boolean canHandle(final HttpRequest httpRequest);

    void service(final HttpRequest httpRequest, final HttpResponse httpResponse);

    void doGet(final HttpRequest httpRequest, final HttpResponse httpResponse);

    void doPost(final HttpRequest httpRequest, final HttpResponse httpResponse);

}
