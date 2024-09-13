package org.apache.catalina.servlet;

import org.apache.coyote.ServletContainer;
import org.apache.coyote.http11.httpmessage.request.HttpRequest;
import org.apache.coyote.http11.httpmessage.response.HttpResponse;

public class PathMatchServletContainer implements ServletContainer {
    private final RequestMapping requestMapping;

    public PathMatchServletContainer(RequestMapping requestMapping) {
        this.requestMapping = requestMapping;
    }

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        Controller controller = requestMapping.getController(request);
        controller.service(request, response);
    }
}
