package org.apache.coyote.http11;

import nextstep.jwp.controller.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class RequestMapping {

    public static HttpResponse createResponse(HttpRequest httpRequest) throws Exception {
        final String requestUri = httpRequest.getRequestUri();
        final AbstractController controller = ControllerMapper.findController(requestUri);
        return controller.getResponse(httpRequest);
    }
}
