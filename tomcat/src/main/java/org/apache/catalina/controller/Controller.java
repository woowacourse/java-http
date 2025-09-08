package org.apache.catalina.controller;

import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;

public interface Controller {

    boolean canHandle(final HttpRequest httpRequest);

    void service(final HttpRequest httpRequest, final HttpResponse httpResponse);

    void doGet(final HttpRequest httpRequest, final HttpResponse httpResponse);

    void doPost(final HttpRequest httpRequest, final HttpResponse httpResponse);

}
