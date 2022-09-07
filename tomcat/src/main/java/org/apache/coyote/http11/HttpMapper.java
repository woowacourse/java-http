package org.apache.coyote.http11;

import java.io.IOException;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.controller.ControllerMapper;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class HttpMapper {

    public static HttpResponse createResponse(HttpRequest httpRequest) throws IOException {
        boolean isAuthorized = true;
        if (httpRequest.containsUri("/login")) {
            isAuthorized = new LoginInterceptor().handle(httpRequest);
        }
        if (!isAuthorized) {
            return ControllerAdvice.handleUnauthorized();
        }

        final String requestUri = httpRequest.getRequestLine();
        final Controller controller = ControllerMapper.findController(requestUri);

        return controller.getResponse(httpRequest);
    }
}
