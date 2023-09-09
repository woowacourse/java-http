package org.apache.coyote.http11.servlet;


import nextstep.jwp.exception.UnauthorizedException;
import org.apache.coyote.http11.exception.NoSuchApiException;
import org.apache.coyote.http11.handler.HandlerAdapterManager;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.Response;
import org.apache.coyote.http11.util.Resource;

public class Servlet {
    public static Response getResponse(Request request){
        try {
            HandlerAdapterManager handlerAdapterManager = new HandlerAdapterManager();
            Response response = handlerAdapterManager.getHandlerAdapter().mapping(request);
            return response;
        } catch (UnauthorizedException unauthorizedException) {
            return Response.badResponse(HttpStatus.UNAUTHORIZED).redirect(Resource.getFile("401.html"),"401.html");
        } catch (NoSuchApiException e){
            return Response.badResponse(HttpStatus.NOTFOUND).redirect(Resource.getFile("404.html"),"404.html");
        }
    }
}
