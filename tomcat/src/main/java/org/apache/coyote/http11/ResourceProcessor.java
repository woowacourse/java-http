package org.apache.coyote.http11;

class ResourceProcessor {

    private final RequestMapping requestMapping;
    private final StaticResourceController staticResourceController;

    public ResourceProcessor(RequestMapping requestMapping, StaticResourceController staticResourceController) {
        this.requestMapping = requestMapping;
        this.staticResourceController = staticResourceController;
    }

    public HttpResponse processResponse(HttpRequest request) throws Exception {
        HttpResponse response = HttpResponse.createHttp11Response();
        boolean hasResource = ResourceFinder.hasResource(request.getPath(), getClass().getClassLoader());
        if (hasResource) {
            return processStaticResource(request, response);
        }

        return processDynamicResource(request, response);
    }

    private HttpResponse processStaticResource(HttpRequest request, HttpResponse response) throws Exception {
        staticResourceController.service(request, response);

        return response;
    }

    private HttpResponse processDynamicResource(HttpRequest request, HttpResponse response) throws Exception {
        Controller targetController = requestMapping.getController(request);
        targetController.service(request, response);

        return response;
    }
}
