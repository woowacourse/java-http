package org.apache.catalina.controller;

import com.techcourse.controller.RequestMapping;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class FrontController implements Controller {
    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        final RequestMapping requestMapping = new RequestMapping();
        if (requestMapping.isRequestMapped(request)) {
            Controller controller = requestMapping.getController(request);
            controller.service(request, response);
            return;
        }
        Controller controller = new ResourceController();
        controller.service(request, response);
    }
}
