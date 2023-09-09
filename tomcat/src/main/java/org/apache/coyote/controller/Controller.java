package org.apache.coyote.controller;

import org.apache.coyote.httprequest.HttpRequest;
import org.apache.coyote.httpresponse.HttpResponse;

public interface Controller {

    void service(final HttpRequest httpRequest, final HttpResponse httpResponse);
}
