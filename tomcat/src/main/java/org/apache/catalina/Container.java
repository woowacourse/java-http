package org.apache.catalina;

import org.apache.catalina.controller.RequestMapping;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class Container {
    private final Manager manager;
    private final RequestMapping requestMapping;

    public Container(Manager manager, RequestMapping requestMapping) {
        this.manager = manager;
        this.requestMapping = requestMapping;
    }

    public void runService(HttpRequest request, HttpResponse response) throws Exception {
        requestMapping.getController(request).service(request, response);
    }

    public Manager getManager() {
        return manager;
    }
}
