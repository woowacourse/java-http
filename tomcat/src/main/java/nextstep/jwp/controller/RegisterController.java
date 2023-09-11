package nextstep.jwp.controller;

import nextstep.jwp.service.UserService;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.StatusLine;

import static org.apache.coyote.http11.request.header.HeaderKey.LOCATION;

public class RegisterController extends AbstractController {

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) throws Exception {
        UserService.register(request.getParameters());

        response.setStatusLine(StatusLine.REDIRECT);
        response.addHeader(LOCATION, "/index");
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        new ResourceController().doGet(request, response);
    }
}
