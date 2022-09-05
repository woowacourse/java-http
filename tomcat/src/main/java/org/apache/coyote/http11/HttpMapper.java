package org.apache.coyote.http11;

import java.io.IOException;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.controller.ControllerMapper;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse1;

public class HttpMapper {

//    public static HttpResponse createResponse(HttpRequest httpRequest) throws IOException {
//        if(httpRequest.containsUri("/login")) {
//            new LoginInterceptor().handle(httpRequest);
//        }
//        return new HttpResponse(httpRequest);
//    }

    public static HttpResponse1 createResponse1(HttpRequest httpRequest) throws IOException {
        boolean isAuthorized = true;
        if (httpRequest.containsUri("/login")) {
            isAuthorized = new LoginInterceptor().handle(httpRequest);
        }

        if (!isAuthorized) {
            return ControllerAdvice.getLoginExceptionResponse();
        }
        final String requestUri = httpRequest.getRequestUri();
        final Controller controller = ControllerMapper.findController(requestUri);

        return controller.getResponse(httpRequest);
    }
}
