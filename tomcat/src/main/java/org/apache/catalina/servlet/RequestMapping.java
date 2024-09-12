package org.apache.catalina.servlet;

import java.util.Map;

import org.apache.catalina.exception.NoMatchedHandlerException;
import org.apache.coyote.http11.httpmessage.request.HttpRequest;

public class RequestMapping {
    private final Map<String, Controller> mappedController;

    public RequestMapping(Map<String, Controller> mappedController) {
        this.mappedController = mappedController;
    }

    public Controller getController(HttpRequest request) {
        if (mappedController.containsKey(request.getTarget())) {
            return mappedController.get(request.getTarget());
        }
        throw new NoMatchedHandlerException(request.getTarget() + " 요청을 처리할 컨트롤러가 존재하지 않습니다.");
    }
}
