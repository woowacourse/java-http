package org.apache.coyote.http11.web;

import java.util.Map;
import nextstep.web.IndexController;
import org.apache.coyote.http11.request.HttpRequestStartLine;

public class HandlerMapping {
    private final Map<String, Controller> controllerMap = Map.of(
            "/index", new IndexController()
    );

    public Controller findController(final HttpRequestStartLine requestStartLine) {
        return controllerMap.get(requestStartLine.getRequestURI());
    }
}
