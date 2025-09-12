package org.apache.coyote.http11.handler;


import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.http.request.dto.HttpRequest;

public interface HandlerMapping {

    int getOrder();

    Controller getHandler(HttpRequest request);
}
