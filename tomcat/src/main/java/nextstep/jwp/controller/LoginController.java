package nextstep.jwp.controller;

import nextstep.jwp.model.User;
import nextstep.jwp.service.UserService;
import org.apache.catalina.servlet.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.header.Cookies;
import org.apache.coyote.http11.request.header.UserSession;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.StatusLine;

import static org.apache.coyote.http11.request.header.HeaderKey.LOCATION;
import static org.apache.coyote.http11.request.header.HeaderKey.SET_COOKIE;

public class LoginController extends AbstractController {

    private static final String INDEX_PAGE = "/index";
    private static final String UNAUTHORIZED_PAGE = "/401";

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) throws Exception {
        try {
            final User user = UserService.login(request.getParameters());
            UserSession.login(request.getSession(), user);

            redirect(response, INDEX_PAGE);
        } catch (Exception e) {
            redirect(response, UNAUTHORIZED_PAGE);
        }
    }

    private void redirect(final HttpResponse response, final String page) {
        response.setStatusLine(StatusLine.REDIRECT);
        response.addHeader(LOCATION, page);
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        if (UserSession.exist(request.getSession())) {
            redirect(response, INDEX_PAGE);
            return;
        }

        response.addHeader(SET_COOKIE, Cookies.createNewSession());
        new ResourceController().doGet(request, response);
    }

}
