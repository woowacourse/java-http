package nextstep.jwp.controller;

import java.io.IOException;
import java.util.UUID;
import nextstep.jwp.LoginFailureException;
import nextstep.jwp.LoginService;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.Session;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class LoginController extends AbstractController {

    private final LoginService loginService;

    public LoginController() {
        this.loginService = new LoginService();
    }

    @Override
    protected HttpResponse doGet(HttpRequest httpRequest) throws IOException {
        if (isExistSession(httpRequest)) {
            return HttpResponse.createWithoutBody(HttpStatus.FOUND, "/index");
        }
        return HttpResponse.createWithBody(HttpStatus.OK, httpRequest.getRequestLine());
    }

    @Override
    protected HttpResponse doPost(HttpRequest httpRequest) throws IOException {
        final String account = httpRequest.getBodyValue("account");
        final String password = httpRequest.getBodyValue("password");

        try {
            final User user = loginService.login(account, password);
            final UUID uuid = UUID.randomUUID();
            final Session session = new Session(uuid.toString());
            session.setAttribute("user", user);
            SessionManager.add(session);
            return HttpResponse.createWithoutBodyForJSession(HttpStatus.FOUND, "/index", uuid.toString());

        } catch (LoginFailureException exception) {
            return ControllerAdvice.handleUnauthorized();
        }
    }

    private boolean isExistSession(HttpRequest httpRequest) {
        final String jSessionId = httpRequest.getCookie().getJSESSIONID();
        return SessionManager.findSession(jSessionId).isPresent();
    }
}
