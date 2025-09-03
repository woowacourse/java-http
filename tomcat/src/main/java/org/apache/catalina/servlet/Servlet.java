package org.apache.catalina.servlet;

import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;

public interface Servlet {

    boolean canHandle(final HttpRequest httpRequest);

    HttpResponse service(final HttpRequest httpRequest);

    HttpResponse doGet(final HttpRequest httpRequest);

    HttpResponse doPost(final HttpRequest httpRequest);

    //TODO: init, destroy
    //TODO: request와  response 같이 넘겨주기 (실제 방식)
}
