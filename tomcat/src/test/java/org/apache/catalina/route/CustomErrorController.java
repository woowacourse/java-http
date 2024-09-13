package org.apache.catalina.route;

import org.apache.catalina.controller.Controller;
import org.apache.coyote.http11.common.HttpStatusCode;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class CustomErrorController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        response.setStatus(HttpStatusCode.INTERNAL_SERVER_ERROR)
                .setBody("Custom Error Handler");
    }
}
