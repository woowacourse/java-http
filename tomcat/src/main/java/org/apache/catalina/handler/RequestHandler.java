package org.apache.catalina.handler;

import http.HttpRequest;
import http.HttpResponse;

public interface RequestHandler {

    HttpResponse service(HttpRequest httpRequest) throws Exception;
}
