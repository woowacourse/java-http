package org.apache.coyote.controller;

import org.apache.coyote.domain.request.HttpRequest;
import org.apache.coyote.domain.response.HttpResponse;

public interface Controller {

    boolean handle(HttpRequest httpRequest);

    void service(HttpRequest httpRequest, HttpResponse httpResponse) throws Exception;
}
