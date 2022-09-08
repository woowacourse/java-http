package nextstep.jwp.controller;

import org.apache.coyote.AbstractController;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.Response;

public class StaticFileController extends AbstractController {

    @Override
    protected void doPost(final Request request, final Response response) throws Exception {
        response.write(HttpStatus.OK, request.getURL());
    }

    @Override
    protected void doGet(final Request request, final Response response) throws Exception {
        response.write(HttpStatus.OK, request.getURL());
    }
}
