package org.apache.catalina.handler;

import http.request.HttpRequest;
import http.response.HttpResponse;

public interface RequestHandler {

    HttpResponse service(HttpRequest httpRequest) throws Exception;
}
