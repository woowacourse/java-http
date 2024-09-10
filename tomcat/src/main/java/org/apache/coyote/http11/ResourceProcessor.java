package org.apache.coyote.http11;

class ResourceProcessor {

    private final RequestMapping requestMapping;

    public ResourceProcessor(RequestMapping requestMapping) {
        this.requestMapping = requestMapping;
    }

    public HttpResponse processResponse(HttpRequest httpRequest) throws Exception {
        HttpResponse httpResponse = HttpResponse.createHttp11Response();
        boolean hasResource = ResourceFinder.hasResource(httpRequest.getPath(), getClass().getClassLoader());
        if (hasResource) {
            processStaticResource(httpRequest, httpResponse);
            return httpResponse;
        }

        return processDynamicResource(httpRequest, httpResponse);
    }

    private void processStaticResource(HttpRequest request, HttpResponse response) throws Exception {
        StaticResourceController staticResourceController = new StaticResourceController();
        staticResourceController.service(request, response);
    }

    private HttpResponse processDynamicResource(HttpRequest httpRequest, HttpResponse httpResponse) throws Exception {
        Controller targetController = requestMapping.getController(httpRequest);
        targetController.service(httpRequest, httpResponse);

        return httpResponse;
    }
}
