package org.apache.catalina;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

import java.net.URL;

import static org.apache.coyote.http11.common.HttpStatus.NOT_FOUND;
import static org.apache.coyote.http11.common.HttpStatus.OK;

public class StaticFileHandler implements Handler {

    @Override
    public void handle(final HttpRequest request, final HttpResponse response) throws Exception {
        String requestURI = request.getRequestLine().getRequestURI();
        URL requestedFile = ClassLoader.getSystemClassLoader().getResource("static" + requestURI);
        if(requestedFile == null) {
            response.httpStatus(NOT_FOUND)
                    .header("Location", "/404.html")
                    .redirectUri("/404.html");
            return;
        }
        response.httpStatus(OK)
                .redirectUri(requestURI);
    }
}
