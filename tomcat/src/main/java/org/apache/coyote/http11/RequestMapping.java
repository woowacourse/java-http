package org.apache.coyote.http11;

import nextstep.jwp.LoginFailureException;
import nextstep.jwp.controller.AbstractController;
import nextstep.jwp.controller.ControllerAdvice;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class RequestMapping {

    public static HttpResponse createResponse(HttpRequest httpRequest) throws Exception {
        final String requestUri = httpRequest.getRequestUri();
        final AbstractController controller = ControllerMapper.findController(requestUri);

        try {
            final HttpResponse response = controller.getResponse(httpRequest);
            return response;
        } catch (LoginFailureException exception) {
            return ControllerAdvice.handleUnauthorized();
        } catch (IllegalArgumentException exception) {
            return ControllerAdvice.handleNotFound();
        }
    }
}
