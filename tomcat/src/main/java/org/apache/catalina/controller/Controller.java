package org.apache.catalina.controller;

import org.apache.coyote.http11.httpmessage.request.HttpRequest;
import org.apache.coyote.http11.httpmessage.response.HttpResponse;

public interface Controller {

    void service(final HttpRequest request, final HttpResponse response) throws Exception;
}
