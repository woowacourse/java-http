package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseEntity;
import org.apache.coyote.util.ResourceUtil;

public class ControllerHandler implements RequestHandler {

    private final HandlerMapping handlerMapping;

    public ControllerHandler(HandlerMapping handlerMapping) {
        this.handlerMapping = handlerMapping;
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
    public HttpResponse handle(HttpRequest httpRequest) {

        return handlerMapping.getController(httpRequest)
                .map(controller -> controller.handle(httpRequest))
                .orElse(ResponseEntity.notFound(""));
    }
}
