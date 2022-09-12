package nextstep.jwp.controller;

import nextstep.jwp.model.User;
import nextstep.jwp.model.UserService;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class RegisterController extends AbstractController {

    private static final RegisterController INSTANCE = new RegisterController();

    private static final String SUCCEED_REDIRECT_URL = "/index.html";
    private static final String REGISTER_PAGE_URL = "/register.html";

    private RegisterController() {
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) {
        response.setOkResponse(REGISTER_PAGE_URL);
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        final User user = UserService.getInstance().register(request.getRequestBody());
        response.setSessionAndCookieWithOkResponse(user, SUCCEED_REDIRECT_URL);
    }

    public static RegisterController getINSTANCE() {
        return INSTANCE;
    }
}
