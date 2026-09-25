package org.apache.catalina.routing;

import org.apache.catalina.controller.Controller;
import org.apache.catalina.controller.StaticResourceController;
import org.apache.coyote.Adapter;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class HttpRequestDispatcher implements Adapter {

    private final RequestMapping requestMapping;
    private final StaticResourceController staticResourceController;

    public HttpRequestDispatcher(final RequestMapping requestMapping,
                                 final StaticResourceController staticResourceController) {
        this.requestMapping = requestMapping;
        this.staticResourceController = staticResourceController;
    }

    @Override
    public HttpResponse service(final HttpRequest request) throws Exception {
        Controller controller = requestMapping.getController(request);
        if (controller == null) {
            controller = staticResourceController;
        }
        return controller.service(request);
    }
}
