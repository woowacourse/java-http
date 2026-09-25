package org.apache.coyote.http11;

public class HttpRequestDispatcher {

    private final RequestMapping requestMapping;
    private final StaticResourceController staticResourceController;

    public HttpRequestDispatcher(final RequestMapping requestMapping,
                                 final StaticResourceController staticResourceController) {
        this.requestMapping = requestMapping;
        this.staticResourceController = staticResourceController;
    }

    public HttpResponse dispatch(final HttpRequest request) throws Exception {
        Controller controller = requestMapping.getController(request);
        if (controller == null) {
            controller = staticResourceController;
        }
        return controller.service(request);
    }
}
