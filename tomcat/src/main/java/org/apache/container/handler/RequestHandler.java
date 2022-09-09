package org.apache.container.handler;

import http.request.HttpRequest;
import http.response.HttpResponse;

public interface RequestHandler {

    HttpResponse service(HttpRequest httpRequest) throws Exception;
}
