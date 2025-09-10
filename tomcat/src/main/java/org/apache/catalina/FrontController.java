package org.apache.catalina;

import com.controller.Controller;
import com.mapping.DynamicControllerMapping;
import com.mapping.StaticControllerMapping;
import org.apache.coyote.http11.Http11Request;
import org.apache.coyote.http11.Http11Response;
import org.apache.coyote.http11.HttpMethod;

public class FrontController {

    private final DynamicControllerMapping dynamicControllerMapping = new DynamicControllerMapping();
    private final StaticControllerMapping staticControllerMapping = new StaticControllerMapping();

    public Http11Response service(final Http11Request request) {
        final String uri = request.getUri();
        final HttpMethod httpMethod = request.getHttpMethod();
        final Controller controller = dynamicControllerMapping.findController(uri, httpMethod);

        if (controller != null) {
            return controller.control(request);
        }

        final Controller staticController = staticControllerMapping.findController(uri);
        if (staticController != null) {
            return staticController.control(request);
        }

        return Http11Response.notFound("text/html;charset=utf-8", "Not Found");
    }
}
