package org.apache.coyote.http11.handler.ready;


import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.request.dto.HttpRequest;

public interface HandlerMapping {

    int getOrder();

    Controller getHandler(HttpRequest request);
}
