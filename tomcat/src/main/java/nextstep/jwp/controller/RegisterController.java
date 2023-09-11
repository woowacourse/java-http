package nextstep.jwp.controller;

import nextstep.jwp.model.User;
import nextstep.jwp.service.UserService;
import org.apache.coyote.http11.AbstractController;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.RequestBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);

    private final UserService userService;

    private RegisterController() {
        userService = UserService.getInstance();
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        response.updatePage(request.getParsedRequestURI());
        response.addHeader("Set-Cookie", "JSESSIONID=" + request.getSessionId());
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) throws Exception {
        final RequestBody requestBody = request.getRequestBody();
        final String account = requestBody.get("account");
        final String email = requestBody.get("email");
        final String password = requestBody.get("password");
        try {
            final User requestUser = new User(account, password, email);
            userService.singUp(requestUser);
            log.info("회원가입 성공 user = {}", requestUser);
            response.updateRedirect(request.getHttpVersion(), "/index.html");
        } catch (IllegalArgumentException e) {
            response.updateRedirect(request.getHttpVersion(), "/register");
        } finally {
            response.addHeader("Set-Cookie", request.getSessionId());
        }
    }

    public static RegisterController getInstance() {
        return Holder.instance;
    }

    private static class Holder {
        public static final RegisterController instance = new RegisterController();
    }
}
