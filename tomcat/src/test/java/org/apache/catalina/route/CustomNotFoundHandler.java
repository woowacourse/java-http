package org.apache.catalina.route;

import org.apache.catalina.controller.Controller;
import org.apache.coyote.http11.common.HttpStatusCode;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class CustomNotFoundHandler implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        response.setStatus(HttpStatusCode.NOT_FOUND)
                .setBody("Custom Not Found");
    }
}
