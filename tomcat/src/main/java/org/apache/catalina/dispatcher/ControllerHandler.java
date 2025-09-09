package org.apache.catalina.dispatcher;

import org.apache.catalina.controller.RequestMapping;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.util.ResourceUtil;

public class ControllerHandler implements RequestHandler {

    private final RequestMapping requestMapping;

    public ControllerHandler(RequestMapping requestMapping) {
        this.requestMapping = requestMapping;
    }

    @Override
    public boolean canHandle(HttpRequest request) {
        // 정적 리소스는 처리하지 않음
        if (ResourceUtil.isStaticResourceExist(request.getPath(), this.getClass())) {
            return false;
        }

        return true;
    }

    @Override
    public void handle(HttpRequest httpRequest, HttpResponse httpResponse) {
        requestMapping.getController(httpRequest)
                .ifPresent(controller -> controller.service(httpRequest, httpResponse));
    }
}
