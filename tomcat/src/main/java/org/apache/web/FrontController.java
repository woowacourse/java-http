package org.apache.web;

import org.apache.coyote.http11.Http11Request;
import org.apache.coyote.http11.Http11Response;
import org.apache.coyote.http11.HttpMethod;

public class FrontController {

    private final ControllerMapping controllerMapping = new ControllerMapping();

    public Http11Response service(final Http11Request request) {
        final String uri = request.getUri();
        final HttpMethod httpMethod = request.getHttpMethod();
        final Controller controller = controllerMapping.findController(uri, httpMethod);

        if (controller == null) {
            return Http11Response.notFound("text/html;charset=utf-8", "Not Found");
        }
        return controller.control(request);
    }
}
