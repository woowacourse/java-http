package org.apache.catalina.handler;


import org.apache.catalina.controller.Controller;
import org.apache.coyote.http11.http.request.dto.HttpRequest;

public interface HandlerMapping {

    int getOrder();

    Controller getHandler(HttpRequest request);
}
