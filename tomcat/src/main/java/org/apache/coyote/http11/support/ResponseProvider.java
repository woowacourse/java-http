package org.apache.coyote.http11.support;

import nextstep.jwp.LoginFailureException;
import org.apache.catalina.Controller;
import nextstep.jwp.controller.ControllerAdvice;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class ResponseProvider {

    public static HttpResponse createResponse(HttpRequest httpRequest) throws Exception {
        final String requestUri = httpRequest.getRequestUri();
        final Controller controller = RequestMapping.find(requestUri);

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
