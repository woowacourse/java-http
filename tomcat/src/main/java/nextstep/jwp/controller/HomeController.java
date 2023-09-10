package nextstep.jwp.controller;

import org.apache.coyote.controller.AbstractController;
import org.apache.coyote.controller.ControllerMapper;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.Response;

public class HomeController extends AbstractController {

    @Override
    protected void doPost(final Request request, final Response response) {
        response.responseNotFound();
    }

    @Override
    protected void doGet(final Request request, final Response response) {
        response.writeBody("Hello world!");
    }
}
