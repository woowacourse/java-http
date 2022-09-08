package org.apache.coyote.http11;

import nextstep.jwp.controller.Controller;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;

public class MethodMapping {

    private final Controller controller;
    private final HttpRequest request;

    private MethodMapping(Controller controller, HttpRequest request) {
        this.controller = controller;
        this.request = request;
    }

    public static MethodMapping from(Controller controller,
                                     HttpRequest request) {
        return new MethodMapping(controller, request);
    }

    public HttpResponse service() throws Exception {
        HttpMethod method = request.method();
        HttpResponse response = HttpResponse.ok();
        if (method.equals(HttpMethod.GET)) {
            controller.doGet(request, response);
            return response;
        }
        if (method.equals(HttpMethod.POST)) {
            controller.doPost(request, response);
            return response;
        }

        response.setStatus(HttpStatus.METHOD_NOT_ALLOWED);
        return response;
    }
}
