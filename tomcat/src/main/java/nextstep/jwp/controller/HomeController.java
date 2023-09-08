package nextstep.jwp.controller;

import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.http11.common.HttpStatus;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class HomeController extends AbstractController {

    private static final String HOME_PAGE = "home.html";

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) {
        response.setHttpStatus(HttpStatus.OK)
                .sendRedirect(HOME_PAGE);
    }
}
