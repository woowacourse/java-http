package org.apache.coyote.http11.dispatcher;

import com.techcourse.ResponseWriters;
import com.techcourse.controller.ViewController;
import org.apache.coyote.http11.dispatcher.handlerAdapter.ControllerHandlerAdapter;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class DispatcherHandler {

    private final ControllerHandlerAdapter controllerHandlerAdapter;
    private final HandlerMapping handlerMapping;
    private final ViewController viewController;

    public DispatcherHandler(ControllerHandlerAdapter controllerHandlerAdapter, HandlerMapping handlerMapping,
                             ViewController viewController) {
        this.controllerHandlerAdapter = controllerHandlerAdapter;
        this.handlerMapping = handlerMapping;
        this.viewController = viewController;
    }

    public HttpResponse doService(HttpRequest httpRequest) throws Exception {
        Object mappingHandler = handlerMapping.getHandler(httpRequest);
        if (controllerHandlerAdapter.supports(mappingHandler)) {
            return controllerHandlerAdapter.handle(httpRequest, mappingHandler);
        }
        HttpResponse httpResponse = new HttpResponse();
        viewController.doGet(httpRequest, httpResponse);

        if (httpResponse.getStatus() == null) {
            ResponseWriters.notFound(httpResponse);
        }
        return httpResponse;
    }
}
