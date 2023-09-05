package org.apache.coyote.http11.handler.controller.base;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public interface Controller {

    HttpResponse service(HttpRequest request) throws Exception;
}
