package nextstep.jwp.controller;

import org.apache.catalina.controller.AbstractController;
import org.apache.catalina.exception.UnsupportedRequestException;
import org.apache.coyote.http11.message.ContentType;
import org.apache.coyote.http11.message.HttpStatus;
import org.apache.coyote.http11.message.request.Request;
import org.apache.coyote.http11.message.response.Response;
import org.apache.coyote.http11.message.response.ResponseBody;

public class RootController extends AbstractController {

    private static final String ROOT_MESSAGE = "Hello world!";

    @Override
    protected void doPost(final Request request, final Response response) throws Exception {
        throw new UnsupportedRequestException();
    }

    @Override
    protected void doGet(final Request request, final Response response) throws Exception {
        final Response createdResponse = Response.createByResponseBody(HttpStatus.OK,
                new ResponseBody(ROOT_MESSAGE, ContentType.HTML));
        response.setBy(createdResponse);
    }
}
