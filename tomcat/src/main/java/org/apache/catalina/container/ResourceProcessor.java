package org.apache.catalina.container;

import jakarta.controller.Controller;
import jakarta.controller.ResourceFinder;
import jakarta.controller.StaticResourceController;
import jakarta.http.HttpRequest;
import jakarta.http.HttpResponse;

public class ResourceProcessor {

    private final RequestMapping requestMapping;
    private final ResourceFinder resourceFinder;
    private final StaticResourceController staticResourceController;

    public ResourceProcessor(
            RequestMapping requestMapping,
            ResourceFinder resourceFinder,
            StaticResourceController staticResourceController) {
        this.requestMapping = requestMapping;
        this.resourceFinder = resourceFinder;
        this.staticResourceController = staticResourceController;
    }

    public HttpResponse processResponse(HttpRequest request) throws Exception {
        HttpResponse response = HttpResponse.createHttpResponse(null);
        boolean hasResource = resourceFinder.hasResource(request.getPath());
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
