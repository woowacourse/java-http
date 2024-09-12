package org.apache.catalina.parser;

import java.util.List;

import org.apache.catalina.mvc.Controller;
import org.apache.catalina.request.HttpRequest;

public class RequestMapping {
    private static final List<Controller> controllers = List.of();
    private static final RequestMapping instance = new RequestMapping();

    private RequestMapping() {}

    public RequestMapping getInstance() {
        return instance;
    }

    public Controller getController(HttpRequest request) {
        return controllers.stream()
                .filter(controller -> controller.matchesRequest(request))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("연결된 컨트롤러가 없습니다."));
    }
}
