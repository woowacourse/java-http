package org.apache.coyote.http11.servlet;


import nextstep.jwp.controller.LoginController;
import nextstep.jwp.exception.UnauthorizedException;
import org.apache.coyote.http11.exception.NoSuchApiException;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.Response;

public class Servlet {
    public static String getResponse(Request request){
        try {
            String uri = request.getUri();
            if (!uri.contains("?") || uri.equals("/")) {
                return getStaticResponse(request).getRequest();
            }
            if (request.getApi().equals("/login")) {
                return LoginController.getLogin(request).redirect("index.html");
            }
        } catch (UnauthorizedException unauthorizedException) {
            return Response.badResponseFrom(HttpStatus.UNAUTHORIZED).redirect("401.html");
        }
        throw new NoSuchApiException("해당 api가 없습니다.");
    }
    private static Response getStaticResponse(Request request){
        return Response.of(HttpStatus.OK, request.getContentType(), request.getResponseBody());
    }
}
