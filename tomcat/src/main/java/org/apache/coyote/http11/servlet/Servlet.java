package org.apache.coyote.http11.servlet;

import nextstep.jwp.exception.UnauthorizedException;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.exception.NoSuchApiException;
import org.apache.coyote.http11.handler.HandlerAdapter;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.Response;
import org.apache.coyote.http11.util.Resource;

public class Servlet {

    private Servlet() {
    }

    public static void getResponse(Request request, Response response) {
        try {
            HandlerAdapter handlerAdapter = HandlerAdapter.getInstance();
            Controller controller = handlerAdapter.mapping(request);
            controller.service(request, response);
        } catch (UnauthorizedException unauthorizedException) {
            response.badResponse(HttpStatus.UNAUTHORIZED, Resource.getFile("401.html"), "401.html");
        } catch (NoSuchApiException e) {
            response.badResponse(HttpStatus.NOTFOUND, Resource.getFile("404.html"), "404.html");
        }
    }
}
