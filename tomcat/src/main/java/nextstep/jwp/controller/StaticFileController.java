package nextstep.jwp.controller;

import org.apache.catalina.controller.AbstractController;
import org.apache.catalina.exception.UnsupportedRequestException;
import org.apache.coyote.http11.message.request.Request;
import org.apache.coyote.http11.message.response.Response;

public class StaticFileController extends AbstractController {

    @Override
    protected void doPost(final Request request, final Response response) throws Exception {
        throw new UnsupportedRequestException();
    }

    @Override
    protected void doGet(final Request request, final Response response) throws Exception {
        final Response createdResponse = Response.createByTemplate(request.getRequestURI());
        response.setBy(createdResponse);
    }
}
