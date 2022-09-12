package nextstep.jwp.controller;

import nextstep.jwp.model.UserService;
import org.apache.coyote.http11.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class LoginController extends AbstractController {

    private static final String FAILED_REDIRECT_URL = "/401.html";
    private static final String LOGIN_HTML_URL = "/login.html";
    private static final LoginController INSTANCE = new LoginController();

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) {
        response.setOkResponse(LOGIN_HTML_URL);
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        try {
            UserService.getInstance().login(request, response);
        } catch (Exception e) {
            response.setFoundResponse(FAILED_REDIRECT_URL);
        }
    }

    public static LoginController getINSTANCE() {
        return INSTANCE;
    }
}
